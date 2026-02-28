import subprocess
import os
from pync import Notifier

def notify(title, subtitle, message):
    script = f'display notification "{message}" with title "{title}" subtitle "{subtitle}"'
    subprocess.call(['osascript',  '-e', script])

# 测试
if __name__ == "__main__":
    script_dir = os.path.dirname(os.path.abspath(__file__))
    icon_path = os.path.join(script_dir,  'jsdc.png')
    # notify("标题", "副标题", "这是一条通知消息")
    Notifier.notify(
        '这是一条通知消息',
        title='标题',
        subtitle=''
    )

    # subprocess.run([
    #     'terminal-notifier',
    #     '-title', '提醒',
    #     '-message', '任务完成'
    # ])
    # subprocess.run(["noti", "-t", "提醒", "-m", "脚本执行完成"])

    # subprocess.run([
    #     'swiftbar',
    #     '--title', '提醒',
    #     '--message', '有新消息',
    #     '--icon', 'path/to/icon.png'
    # ])




