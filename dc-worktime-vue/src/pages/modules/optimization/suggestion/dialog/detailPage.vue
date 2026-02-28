<template>
	<ElDescriptions :column="2">
		<ElDescriptionsItem label="项目名称：">{{ detail.pro_project_name || "--" }}</ElDescriptionsItem>
		<ElDescriptionsItem label="标题：">{{ detail.title || "--" }}</ElDescriptionsItem>
		<ElDescriptionsItem label="评分：">
			<template v-if="detail.mark">
				<ElRate
					:allow-half="true"
					v-model="detail.mark"
					disabled
					show-score
					:score-template="`${Number(detail.mark) || 0} 分`"
				></ElRate>
			</template>
			<template v-else>--</template>
		</ElDescriptionsItem>
		<ElDescriptionsItem label="结果：">
			<span v-if="detail.markResult == 0">不采纳</span>
			<span v-else-if="detail.markResult == 1">采纳</span>
			<span v-else>--</span>
		</ElDescriptionsItem>
		<ElDescriptionsItem label="附件：" :span="2">
			<!-- 判断结果是否有值 -->

			<template v-if="detail.files && detail.files.length > 0">
				<div class="inline-flex gap-10px" style="flex-wrap: wrap; overflow-y: auto; overflow-x: hidden">
					<ElLink type="primary" v-for="item in detail.files" @click="downFile(item.fileUrl, item.fileName)">
						{{ item.fileName }}
					</ElLink>
				</div>
			</template>
			<template v-else>--</template>
		</ElDescriptionsItem>
		<ElDescriptionsItem :span="2" label="内容：">
			<div ref="descRef" class="description-content" v-html="detail.content"></div>
		</ElDescriptionsItem>
		<div v-viewer="viewerOptions" v-if="previewVisible">
			<img v-for="(url, index) in previewList" :key="index" :src="url" />
		</div>
	</ElDescriptions>
</template>

<script setup lang="ts">
	import { ElDescriptions, ElDescriptionsItem, ElImageViewer, ElCard, ElRate, ElLink } from "element-plus";
	import { selectById } from "@/api/optimization/suggestion";
	import BaseForm from "@/core/struct/form/base-form";
	import { useDialogStructForm } from "@/core/struct/form/use-base-form";
	import { onMounted, ref, toRaw, watch, nextTick } from "vue";
	import { downFile } from "@/core/utils";
	import "viewerjs/dist/viewer.css";
	import { api as viewerApi } from "v-viewer";
	const detail = ref<any>({});
	const props = defineProps({
		rowId: {
			type: Number,
			default: 0
		}
	});

	// 预览控制
	const previewVisible = ref(false);
	const previewList = ref<string[]>([]); // 图片URL集合
	const currentIndex = ref(0); // 当前图片索引
	const descRef = ref<HTMLElement>(); // 富文本容器引用

	// v-viewer 选项
	const viewerOptions = ref({
		inline: false,
		viewed() {
			this.viewer.zoomTo(1);
		},
		zIndex: 999999
	});

	// 提取富文本中所有图片URL
	const updatePreviewList = () => {
		nextTick(() => {
			const images = descRef.value?.querySelectorAll("img");
			previewList.value = Array.from(images || []).map((img) => img.src);
		});
	};

	// 绑定图片点击事件
	const bindImageClick = () => {
		nextTick(() => {
			const images = descRef.value?.querySelectorAll("img");
			images?.forEach((img, index) => {
				img.style.cursor = "zoom-in";
				img.onclick = (e) => {
					e.stopPropagation();
					currentIndex.value = index;
					// 使用 viewerApi 实现图片预览
					viewerApi({
						images: previewList.value,
						options: {
							...viewerOptions.value,
							inline: false,
							zIndex: 999999,
							initialViewIndex: index
						}
					});
				};
			});
		});
	};

	// 初始化
	onMounted(async () => {
		const res = await selectById({
			id: props.rowId
		});
		detail.value = res;
		updatePreviewList();
		bindImageClick();
	});

	// 监听数据变化
	watch(
		() => props.detail,
		() => {
			updatePreviewList();
			bindImageClick();
		}
	);
</script>
<style scoped lang="scss">
	.description-content {
		:deep(img) {
			max-width: 100%;
			height: auto;
			margin: 4px 0;
			border-radius: 4px;
			transition: transform 0.2s;
			cursor: zoom-in;
		}
	}
</style>
