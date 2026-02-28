<template>
	<QuillEditor ref="quill" v-model:content="contentValue" :options="editorOptions" @blur="handleBlur" />
	<!-- <img :src="url" alt="" srcset="" /> -->
</template>

<script setup lang="ts">
	import { Quill, QuillEditor } from "@vueup/vue-quill";
	import type { QuillOptions } from "quill";
	import { ref, watch, onMounted, h, render } from "vue";
	import { object, string } from "vue-types";
	import { defu } from "defu";
	import { uploadFile } from "@/api/common/login";
	import "viewerjs/dist/viewer.css";
	import { api as viewerApi } from "v-viewer";
	// @ts-ignore
	// import { ImageExtend, QuillWatch } from "quill-image-extend-module";
	// @ts-ignore
	// import ImageResize from "quill-image-resize-module";

	const quill = ref<InstanceType<typeof QuillEditor>>();
	// Quill.register("modules/imageResize", ImageResize);
	// Quill.register("modules/ImageExtend", ImageExtend);
	const props = defineProps({
		options: object<QuillOptions>().def({}),
		modelValue: string().def()
	});
	const emit = defineEmits(["update:modelValue", "blur"]);

	const handleBlur = (e: any) => {
		emit("blur", e);
	};

	const imageHandler = (isImage: boolean) => {
		if (isImage) {
			// 自定义上传文件
			const input = document.createElement("input");
			input.setAttribute("multiple", "true");
			input.setAttribute("type", "file");
			input.setAttribute("accept", ".jpg,.png,.jpeg");
			input.style.display = "none";
			input.addEventListener("change", (e: any) => {
				const files = [...e.target.files] as File[];
				if (files.length > 0) {
					// 上传文件
					uploadFile(files).then((res: any) => {
						// 将文件添加到文本编辑器中
						const quillInstance = quill.value.getQuill() as Quill;
						let length = quillInstance.getSelection()?.index || 0;
						quillInstance.insertEmbed(length, "image", res.fileUrl); // 调整光标到最后
						quillInstance.setSelection(length + 1);
					});
				}
			});
			input.addEventListener("cancel", (e) => {
				document.body.removeChild(input);
			});
			document.body.appendChild(input);
			input.click();
		}
	};

	const editorOptions = defu(props.options, {
		theme: "snow",
		placeholder: "请输入",

		modules:
			props.options && props.options.readOnly
				? { toolbar: false }
				: {
						// 富文本头部栏的功能配置
						toolbar: {
							container: [
								["bold", "italic", "underline", "strike"], // 加粗 斜体 下划线 删除线
								[{ color: [] }, { background: [] }], // 字体颜色、字体背景颜色
								[{ align: [] }], // 对齐方式
								[
									{
										size: ["small", false, "large", "huge"]
									}
								], // 字体大小
								[{ font: [] }], // 字体种类
								[{ header: [1, 2, 3, 4, 5, 6, false] }], // 标题
								[{ direction: "ltl" }], // 文本方向
								[{ direction: "rtl" }], // 文本方向
								[{ indent: "-1" }, { indent: "+1" }], // 缩进
								[{ list: "ordered" }, { list: "bullet" }], // 有序、无序列表
								[{ script: "sub" }, { script: "super" }], // 上标/下标
								["blockquote", "code-block"], // 引用  代码块
								["clean"], // 清除文本格式
								["link"], // 链接、图片、视频
								["image"] // 图片
							],
							handlers: {
								image: imageHandler // 点击图片触发事件
							}
						}
						// imageResize: {} // 声明imageResize模块，必须的！
					}
	});

	const contentValue = ref<string>();

	watch(
		() => contentValue.value,
		(value: any) => {
			emit("update:modelValue", value);
		}
	);

	watch(
		() => props.modelValue,
		(newVal) => {
			contentValue.value = newVal;
		},
		{ immediate: true }
	);

	onMounted(() => {
		const quillInstance = quill.value?.getQuill();
		if (!quillInstance) return;

		quillInstance.root.addEventListener("paste", async (e) => {
			const clipboardData = e.clipboardData;
			if (!clipboardData) return;

			// 标记是否有图片处理
			let hasImageProcessed = false;

			// 处理直接粘贴的图片文件
			const items = clipboardData.items;
			const imageFiles: File[] = [];
			for (const item of items) {
				if (item.kind === "file" && item.type.startsWith("image/")) {
					const file = item.getAsFile();
					if (file) imageFiles.push(file);
				}
			}

			// 处理HTML中的Base64图片
			let processedHtml = clipboardData.getData("text/html");
			if (processedHtml) {
				const parser = new DOMParser();
				const doc = parser.parseFromString(processedHtml, "text/html");
				const images = doc.querySelectorAll("img");
				const base64Images: { src: string; blob: Blob }[] = [];

				for (const img of images) {
					const src = img.getAttribute("src");
					if (src?.startsWith("data:")) {
						const blob = dataURLtoBlob(src);
						base64Images.push({ src, blob });
					}
				}

				if (base64Images.length > 0) {
					hasImageProcessed = true;
					e.preventDefault();
					const uploadPromises = base64Images.map(async ({ blob }) => {
						const file = new File([blob], "pasted-image.png", { type: blob.type });
						const res = await uploadFile([file]);
						return res.fileUrl;
					});

					const urls = await Promise.all(uploadPromises);
					base64Images.forEach(({ src }, index) => {
						processedHtml = processedHtml.replace(src, urls[index]);
					});
				}
			}

			// 处理直接粘贴的图片文件
			if (imageFiles.length > 0) {
				hasImageProcessed = true;
				e.preventDefault();
				const res = await uploadFile(imageFiles);
				const urls = Array.isArray(res) ? res.map((r) => r.fileUrl) : [res.fileUrl];
				const range = quillInstance.getSelection();
				let index = range?.index || 0;
				urls.forEach((url) => {
					quillInstance.insertEmbed(index, "image", url);
					index++;
				});
				quillInstance.setSelection(index);
			}

			// 如果有图片处理，手动插入处理后的HTML或文本
			if (hasImageProcessed) {
				if (processedHtml) {
					const delta = quillInstance.clipboard.convert(processedHtml);
					quillInstance.updateContents(delta);
				} else {
					const text = clipboardData.getData("text/plain");
					quillInstance.insertText(quillInstance.getSelection().index, text);
				}
			} else {
				// 没有图片时，允许默认粘贴行为
				return;
			}
		});

		quillInstance.root.addEventListener("click", async (e) => {
			// 实现图片放大功能
			const target = e.target as HTMLElement;
			if (target.tagName.toLowerCase() === "img") {
				const src = target.getAttribute("src");
				if (src) {
					// console.log(src);
					// const f = document.createDocumentFragment();
					// const instance = h(ElImage, { src, previewSrcList: [src] });
					// render(instance, f);
					// document.body.appendChild(f);
					// // instance.el.click();
					// console.log(instance);
					const iiew = viewerApi({
						images: [src],
						options: {
							inline: false,
							zIndex: 999999
						}
					});
					// iiew.show();
				}
			}
		});
	});

	// 将Base64转换为Blob
	function dataURLtoBlob(dataurl: string) {
		const arr = dataurl.split(",");
		const mimeMatch = arr[0].match(/:(.*?);/);
		if (!mimeMatch) throw new Error("Invalid data URL");
		const mime = mimeMatch[1];
		const bstr = atob(arr[1]);
		let n = bstr.length;
		const u8arr = new Uint8Array(n);
		while (n--) {
			u8arr[n] = bstr.charCodeAt(n);
		}
		return new Blob([u8arr], { type: mime });
	}
</script>

<style lang="css" scoped></style>
