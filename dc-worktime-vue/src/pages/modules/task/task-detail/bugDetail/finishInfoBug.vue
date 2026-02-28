<template>
	<ElDescriptions :column="2" label-width="120px">
		<ElDescriptionsItem label="任务开始时间：">{{ detail.start_time || "--" }}</ElDescriptionsItem>
		<ElDescriptionsItem label="任务完成时间：">{{ detail.completion_time || "--" }}</ElDescriptionsItem>
		<!-- <ElDescriptionsItem label="任务延期情况：">{{ detail.delayStatusName || "--" }}</ElDescriptionsItem> -->
		<ElDescriptionsItem label="复测次数：">{{ detail.retestsNum || "--" }}</ElDescriptionsItem>
		<ElDescriptionsItem label="复测解决类型：">{{ retestsTypeText }}</ElDescriptionsItem>
		<ElDescriptionsItem :span="2" label="解决说明：">
			<div
				v-if="detail.description"
				style="display: flex; background: rgba(0, 0, 0, 0.04); padding: 10px 16px; margin-top: 10px"
			>
				<span ref="descRef" class="desc-img" v-html="detail.retestsContent"></span>
			</div>
		</ElDescriptionsItem>
		<div v-viewer="viewerOptions" v-if="previewVisible">
			<img v-for="(url, index) in previewList" :key="index" :src="url" />
		</div>
	</ElDescriptions>
</template>
<script setup lang="ts">
	import { ElDescriptions, ElDescriptionsItem } from "element-plus";
	import { computed, ref, watch, nextTick, onMounted } from "vue";
	import "viewerjs/dist/viewer.css";
	import { api as viewerApi } from "v-viewer";

	const props = defineProps({
		detail: {
			type: Object,
			default: {}
		},
		testType: {
			type: String,
			default: ""
		}
	});

	const retestsTypeText = computed(() => {
		switch (props.detail.retestsType) {
			case 1:
				return "已解决";
			case 2:
				if (props.testType == "3" || props.testType == "4") {
					return "这不是问题";
				}
				return "这不是bug";
			default:
				return "--";
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
	onMounted(() => {
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
	.desc-img > p {
		margin: 0;
	}
	.desc-img {
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
