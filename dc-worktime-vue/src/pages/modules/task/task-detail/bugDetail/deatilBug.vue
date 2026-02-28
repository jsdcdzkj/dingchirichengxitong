<template>
	<ElDescriptions :column="2" label-width="120px">
		<ElDescriptionsItem :label="titleType + '提出人：'">{{ detail.create_user_name || "--" }}</ElDescriptionsItem>
		<ElDescriptionsItem label="严重程度：">{{ detail.severityName || "--" }}</ElDescriptionsItem>
		<ElDescriptionsItem label="提出时间：">{{ detail.createTime || "--" }}</ElDescriptionsItem>
		<ElDescriptionsItem :label="titleType + '来源：'">{{ detail.sourceName || "--" }}</ElDescriptionsItem>
		<ElDescriptionsItem label="里程碑名称：">{{ detail.reviewName || "--" }}</ElDescriptionsItem>
		<ElDescriptionsItem v-if="moduleShow" label="所属项目：">{{
			detail.pro_project_name || "--"
		}}</ElDescriptionsItem>
		<ElDescriptionsItem v-if="moduleShow" label="所属模块：">{{ detail.moduleName || "--" }}</ElDescriptionsItem>
		<ElDescriptionsItem label="功能名称：">{{ detail.featureName || "--" }}</ElDescriptionsItem>
		<ElDescriptionsItem label="任务描述：">
			<div
				v-if="detail.description"
				style="display: flex; background: rgba(0, 0, 0, 0.04); padding: 10px 16px; margin-top: 10px"
			>
				<span ref="descRef" class="desc-img" v-html="detail.description"></span>
			</div>
		</ElDescriptionsItem>
		<div v-viewer="viewerOptions" v-if="previewVisible">
			<img v-for="(url, index) in previewList" :key="index" :src="url" />
		</div>
	</ElDescriptions>
</template>

<script setup lang="ts">
	import { ElDescriptions, ElDescriptionsItem } from "element-plus";
	import { ref, watch, nextTick, onMounted } from "vue";
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

	const titleType = ref("bug");
	const moduleShow = ref(true);
	watch(() => props.testType, () => {
		if (props.testType == "3" || props.testType == "4") {
			titleType.value = "问题";
		} else {
			titleType.value = "bug";
		}
		if (props.testType == "4") {
			moduleShow.value = false;
		}
	})

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

	.el-descriptions__label:not(.is-bordered-label) {
		color: rgba(0, 0, 0, 0.85);
	}
	.el-descriptions__content:not(.is-bordered-label) {
		color: rgba(0, 0, 0, 0.65);
	}
</style>
