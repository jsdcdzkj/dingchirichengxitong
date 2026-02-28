// 导出一个名为 downFile 的函数，用于下载文件
export const downFile = (fileUrl: string, fileName?: string) => {
	// 创建一个 <a> 元素
	const a = document.createElement("a");
	// 将 fileUrl 按照斜杠分割成一个数组
	const list = fileUrl.split("/");
	// 设置 <a> 元素的 href 属性为文件的 URL
	a.href = fileUrl;
	// 设置 <a> 元素的 download 属性，如果提供了 fileName 则使用 fileName，否则使用 URL 中的最后一个部分作为文件名
	a.download = fileName ? fileName : list[list.length - 1];
	// 模拟点击 <a> 元素，触发文件下载
	a.click();
};
