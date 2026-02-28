import os
import subprocess


def build():
    # 获取当前脚本的路径
    script_name = 'main_mac.py'  # 替换为你的 Python 脚本名
    dist_folder = 'dist'
    icon_file = 'jsdc.icns'  # macOS 使用 .icns 格式的图标文件
    app_name = '鼎驰工时系统'

    # macOS 特定的打包参数
    command = [
        'pyinstaller',
        '--onefile',  # 生成单个可执行文件
        '--icon', icon_file,  # 添加图标（必须是 .icns 格式）
        '--name', app_name,  # 应用名称
        '--windowed',  # 对 macOS 相当于 --noconsole
        '--osx-bundle-identifier', 'com.yourcompany.yourapp',  # 可选：设置 bundle identifier

        # 确保包含 plyer 库及其依赖
        '--hidden-import=plyer',
        '--hidden-import=plyer.platforms',
        '--hidden-import=plyer.platforms.darwin',
        '--hidden-import=plyer.platforms.darwin.notification',

        '--add-data', f'{icon_file}:.',  # macOS 使用 : 作为路径分隔符
        '--clean',  # 清理中间文件
        '--distpath', dist_folder,  # 设置输出路径为 dist 文件夹
        '--workpath', 'build',  # 设置临时文件存储路径
        '--specpath', '.',  # 保存 .spec 文件到当前目录

        script_name  # 要打包的 Python 脚本文件名
    ]

    # 执行命令
    print(f"正在打包 {script_name}...")
    subprocess.run(command)

    print(f"打包完成，可执行文件位于 {dist_folder} 文件夹内")


if __name__ == '__main__':
    build()