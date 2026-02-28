import sys
import socket
import os
import json
import asyncio
import winreg as reg
import requests
import websockets
from PyQt5.QtCore import Qt, QThread, pyqtSignal, QSettings,QSharedMemory,QTimer
from PyQt5.QtGui import QIcon
from PyQt5.QtWidgets import (
    QApplication, QWidget, QVBoxLayout, QLineEdit, QPushButton, QLabel,
    QSpacerItem, QSizePolicy, QSystemTrayIcon, QMenu, QAction, QMessageBox
)
from qt_material import apply_stylesheet
import webbrowser

# 获取打包后的文件路径
if hasattr(sys, '_MEIPASS'):
    icon_path = os.path.join(sys._MEIPASS, 'jsdc.ico')  # 打包后的临时文件夹路径
else:
    icon_path = 'jsdc.ico'  # 开发环境中的相对路径
# 获取本机 IP
def get_local_ip():
    s = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
    s.settimeout(0)
    try:
        s.connect(('10.254.254.254', 1))
        ip = s.getsockname()[0]
    except Exception:
        ip = '127.0.0.1'
    finally:
        s.close()
    return ip

websocket_ip="management.dincher.cn"
class WebSocketClient(QThread):
    message_received = pyqtSignal(str)  # 定义信号用于通知 UI 线程
    connected = pyqtSignal(bool)  # 连接状态信号

    def __init__(self, username,realname, server_uri):
        super().__init__()
        self.username = username
        self.realname = realname
        self.server_uri = server_uri
        self.websocket = None
        self.running = True

    async def websocket_handler(self):
        while self.running:
            try:
                async with websockets.connect(self.server_uri) as websocket:
                    self.websocket = websocket
                    self.connected.emit(True)  # 连接成功
                    await websocket.send(f"LOGIN:{self.username}&{self.realname}")

                    async for message in websocket:
                        self.message_received.emit(message)  # 发送消息到 UI 线程
            except Exception as e:
                print(f"WebSocket 连接异常: {e}")
                self.connected.emit(False)  # 连接失败
                await asyncio.sleep(3)  # 5 秒后重试

    def run(self):
        asyncio.run(self.websocket_handler())

    def stop(self):
        loop = asyncio.get_event_loop()
        future = asyncio.run_coroutine_threadsafe(self.websocket.close(), loop)
        # try:
        #     future.result()  # 阻塞直到协程完成
        #     print("WebSocket connection closed successfully.")
        # except Exception as e:
        #     print(f"Error closing WebSocket connection: {e}")
    # def stop(self):
    #     self.running = False
    #     if self.websocket:
    #         # asyncio.run(self.websocket.close())
    #         asyncio.run_coroutine_threadsafe(self.websocket.close(), asyncio.get_event_loop())


class LoginWindow(QWidget):
    def __init__(self):
        super().__init__()

        self.settings = QSettings("MyCompany", "WorkHourClient")
        self.websocket_thread = None
        self.tray_icon = None

        saved_username = self.settings.value("username", "")
        if saved_username:
            QTimer.singleShot(1000, lambda: self.login())

        self.setWindowTitle('鼎驰工时系统')
        self.setFixedSize(350, 150)

        layout = QVBoxLayout()

        # 工号输入框
        self.username_input = QLineEdit(self)
        self.username_input.setPlaceholderText('请输入工号')
        self.username_input.setMaxLength(20)
        self.username_input.setAlignment(Qt.AlignCenter)  # 居中输入框内容
        self.username_input.setStyleSheet("""
            QLineEdit {
                border: 2px solid #4CAF50;
                border-radius: 15px;
                padding: 10px;
                font-size: 14px;
            }
            QLineEdit:focus {
                border-color: #2196F3;
            }
        """)
        saved_username = self.settings.value("username", "")
        if saved_username:
            self.username_input.setText(saved_username)
        layout.addWidget(self.username_input)

        # 连接状态标签
        self.status_label = QLabel("未连接")
        self.status_label.setAlignment(Qt.AlignCenter)
        self.status_label.setStyleSheet("""
            QLabel {
                color: red;
                font-size: 16px;
                font-weight: bold;
            }
        """)
        layout.addWidget(self.status_label)

        # 登录按钮
        self.login_button = QPushButton('登录', self)
        self.login_button.setStyleSheet("""
            QPushButton {
                background-color: #4CAF50;
                border-radius: 15px;
                color: white;
                font-size: 16px;
                padding: 10px;
                border: none;
            }
            QPushButton:hover {
                background-color: #388E3C;
            }
            QPushButton:pressed {
                background-color: #2C6B2F;
            }
            QPushButton:disabled {
                background-color: #B0BEC5;
            }
        """)
        self.login_button.clicked.connect(self.login)
        layout.addWidget(self.login_button)

        # 底部IP提示
        self.ip_label = QLabel(f"本机IP: {get_local_ip()}")
        self.ip_label.setAlignment(Qt.AlignCenter)
        self.ip_label.setStyleSheet("""
            QLabel {
                color: #3229C5;
                font-size: 14px;
                font-weight: normal;
            }
        """)
        layout.addWidget(self.ip_label)

        # 添加间隔，确保 IP 在底部
        spacer = QSpacerItem(20, 40, QSizePolicy.Minimum, QSizePolicy.Expanding)
        layout.addItem(spacer)

        self.setLayout(layout)
        app.setWindowIcon(QIcon(icon_path))

        # 托盘图标
        if self.tray_icon is None:  # 避免重复创建
            self.tray_icon = QSystemTrayIcon(QIcon(icon_path), self)
            self.tray_icon.setToolTip("工时系统")

            tray_menu = QMenu()
            show_action = QAction("显示窗口", self)
            show_action.triggered.connect(self.show_window)
            tray_menu.addAction(show_action)

            logout_action = QAction("退出账号", self)
            logout_action.triggered.connect(self.logout_window)
            tray_menu.addAction(logout_action)

            exit_action = QAction("退出", self)
            exit_action.triggered.connect(self.quit_app)
            tray_menu.addAction(exit_action)

            self.tray_icon.setContextMenu(tray_menu)
            self.tray_icon.show()
            # 绑定双击事件
            self.tray_icon.activated.connect(self.on_tray_icon_activated)
            self.tray_icon.messageClicked.connect(self.open_url)

    def login(self):
        username = self.username_input.text().strip()
        resultCode = ""
        realname = ""
        if username:
            url = "https://"+websocket_ip+"/api/login_client"  # 替换为实际的接口地址
            data = {
                "username": username,
            }
            try:
                response = requests.post(url, data=data, timeout=2)
                if response.status_code == 200:
                    resultinfo = response.json()
                    resultCode = resultinfo["code"]
                    realname = resultinfo["data"]
            except requests.exceptions.Timeout:
                msg_box = QMessageBox()
                msg_box.setIcon(QMessageBox.Warning)
                msg_box.setWindowTitle("登录失败")
                msg_box.setText("请求超时，请检查网络或服务器状态！")
                msg_box.setStandardButtons(QMessageBox.Ok)
                msg_box.exec_()
                return
            except requests.exceptions.RequestException as e:
                msg_box = QMessageBox()
                msg_box.setIcon(QMessageBox.Warning)
                msg_box.setWindowTitle("登录失败")
                msg_box.setText(f"请求失败: {e}")
                msg_box.setStandardButtons(QMessageBox.Ok)
                msg_box.exec_()
                return
        else:
            self.status_label.setText("请输入工号！")

        if resultCode == 200:
            self.settings.setValue("username", username)  # 记住工号
            self.settings.setValue("realname", realname)  # 记住姓名
            # self.login_button.setEnabled(False)  # 防止重复点击
            self.status_label.setText("连接中...")
            self.status_label.setStyleSheet("color: orange; font-size: 12px;")

            self.websocket_thread = WebSocketClient(username, realname, "wss://"+websocket_ip+"/ws")
            self.websocket_thread.message_received.connect(self.show_notification)
            self.websocket_thread.connected.connect(self.update_status)
            self.websocket_thread.start()

            self.login_button.setDisabled(True)  # 禁用按钮

            self.username_input.setEnabled(False)


            self.hide()  # 隐藏窗口
        else:
            self.status_label.setText("无此用户！")

    def on_tray_icon_activated(self, reason):
        """处理托盘图标事件"""
        if reason == QSystemTrayIcon.DoubleClick:
            self.show_window()  # 双击托盘图标时，显示窗口

    def open_url(self):
        """ 打开指定的 URL """
        if hasattr(self, 'notification_url') and self.notification_url:
            webbrowser.open(self.notification_url)
    def update_status(self, is_connected):
        if is_connected:
            realname = self.settings.value("realname")
            self.status_label.setText("用户"+realname+"已连接")
            self.status_label.setStyleSheet("color: green; font-size: 12px;")
        else:
            self.status_label.setText("连接失败，尝试重连...")
            self.status_label.setStyleSheet("color: red; font-size: 12px;")

    def show_notification(self, message):
        self.notification_url = "https://management.dincher.cn/task/kanban"
        """使用 QSystemTrayIcon 显示消息，避免 win10toast 额外气泡图标"""
        try:
            msg_data = json.loads(message)
            title = msg_data.get("title", "新消息")
            body = msg_data.get("body", "无内容")

            if self.tray_icon:
                self.tray_icon.showMessage(title, body, QSystemTrayIcon.Information, 5000)  # 5 秒后消失
        except json.JSONDecodeError:
            print("收到的消息不是有效的 JSON 格式")

    def show_window(self):
        self.showNormal()
        self.activateWindow()  # 让窗口置顶

    def logout_window(self):
        if self.websocket_thread:
            try:
                asyncio.run(self.websocket_thread.websocket.send("DISCONNECT:" + self.settings.value("username")))
                print("DISCONNECT message sent successfully")
            except Exception as e:
                print(f"Failed to send DISCONNECT message: {e}")
        self.settings.remove("username")  # 删除账号
        self.show_window()  # 显示登录窗口
        self.username_input.clear()  # 清空输入框
        self.status_label.setText(" 未连接")  # 重置状态标签
        self.login_button.setEnabled(True)  # 启用登录按钮
        self.username_input.setEnabled(True)  # 启用输入框



    def closeEvent(self, event):
        reply = QMessageBox(self)
        reply.setWindowTitle("关闭窗口")
        reply.setText("是否最小化到托盘？\n选择“是”将隐藏窗口到托盘，选择“否”将退出程序。")
        reply.setStandardButtons(QMessageBox.Yes | QMessageBox.No)
        reply.setDefaultButton(QMessageBox.Yes)

        # 修改按钮文本
        reply.button(QMessageBox.Yes).setText("是")
        reply.button(QMessageBox.No).setText("否")

        result = reply.exec()

        if result == QMessageBox.Yes:
            event.ignore()  # 忽略关闭事件，隐藏到托盘
            self.hide()
            self.tray_icon.showMessage("提示", "程序已最小化到托盘", QSystemTrayIcon.Information, 2000)
        else:
            event.accept()  # 允许窗口关闭

    def quit_app(self):
        if self.websocket_thread:
            self.websocket_thread.stop()
        QApplication.quit()
        os._exit(0)

def add_to_startup():
    """添加到Windows启动项"""
    # 如果是打包后的exe程序
    if getattr(sys, 'frozen', False):
        # 获取exe文件的完整路径
        exe_path = sys.executable
    else:
        # 如果是直接运行Python脚本
        exe_path = os.path.abspath(sys.argv[0])
    app_name = "鼎驰工时系统"  # 应用程序的名称
    try:
        # 注册到 Windows 启动项
        key = reg.HKEY_CURRENT_USER
        sub_key = r"Software\Microsoft\Windows\CurrentVersion\Run"
        with reg.OpenKey(key, sub_key, 0, reg.KEY_WRITE) as reg_key:
            reg.SetValueEx(reg_key, app_name, 0, reg.REG_SZ, exe_path)
            print(f"{app_name} 已添加到开机启动项。")
    except Exception as e:
        print(f"设置自启失败: {e}")

if __name__ == '__main__':
    sys.argv[0] = "鼎驰工时系统"
    app = QApplication(sys.argv)
    shared_memory = QSharedMemory("MyUniqueAppInstance")

    if not shared_memory.create(1):  # 如果创建失败，说明已有实例在运行
        msg_box = QMessageBox()
        msg_box.setIcon(QMessageBox.Warning)
        msg_box.setWindowTitle("提示")
        msg_box.setText("程序已经在运行！")
        msg_box.setStandardButtons(QMessageBox.Ok)
        msg_box.exec_()
        sys.exit(0)
    apply_stylesheet(app, theme='light_blue.xml')
    window = LoginWindow()
    window.show()
    # 设置开机自启
    add_to_startup()  # 启用自启动
    sys.exit(app.exec_())
