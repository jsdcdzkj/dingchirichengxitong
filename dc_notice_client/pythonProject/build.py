import os
import subprocess

def build():
    # 获取当前脚本的路径
    script_name = 'main.py'  # 替换为你的 Python 脚本名
    dist_folder = 'dist'
    icon_file = 'jsdc.ico'  # 确保图标文件位于同一目录或提供完整路径
    app_name = '鼎驰工时系统'


    # 执行 PyInstaller 打包命令
    command = [
        'pyinstaller',
        '--onefile',  # 生成单个可执行文件
        '--icon', icon_file,  # 添加图标
        '--name', app_name,  # exe名称
        '--hidden-import=plyer',  # 确保包含 plyer 库
        '--add-data', 'jsdc.ico;.',  # 将 jsdc.ico 文件添加到打包文件中
        '--hidden-import=plyer.platforms',  # 确保包含 plyer 库
        '--hidden-import=plyer.platforms.win',  # 确保包含 plyer 库
        '--hidden-import=plyer.platforms.win.notification',  # 确保包含 plyer 库
        '--clean',  # 清理中间文件
        '--distpath', dist_folder,  # 设置输出路径为 dist 文件夹
        '--workpath', 'build',  # 设置临时文件存储路径
        '--specpath', '.',  # 保存 .spec 文件到当前目录
        '--noconsole',  # 不显示控制台窗口

        script_name  # 要打包的 Python 脚本文件名
    ]

    # 执行命令
    print(f"正在打包 {script_name}...")
    subprocess.run(command)

    print(f"打包完成，可执行文件位于 {dist_folder} 文件夹内")


if __name__ == '__main__':
    build()
