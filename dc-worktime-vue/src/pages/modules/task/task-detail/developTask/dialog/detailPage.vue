<template>
	<ElRow :gutter="24">
		<ElCol :span="12">
			<div class="desc-item">
				<div class="desc-title">父节点</div>
				<div class="desc-content">{{ rowInfo.module_name || "--" }}</div>
			</div>
			<!-- <ElDescriptions :column="1" border class="m-b-24px">
				<ElDescriptionsItem label="父节点">{{ detail.module_name || "--" }}</ElDescriptionsItem>
			</ElDescriptions> -->
		</ElCol>
		<ElCol :span="12">
			<div class="desc-item">
				<div class="desc-title">功能名称</div>
				<div class="desc-content">{{ detail.feature_name || "--" }}</div>
			</div>
			<!-- <ElDescriptions :column="1" border class="m-b-24px">
				<ElDescriptionsItem label="功能名称">{{ detail.feature_name || "--" }}</ElDescriptionsItem>
			</ElDescriptions> -->
		</ElCol>
		<ElCol :span="24">
			<div class="desc-item">
				<div class="desc-title">功能描述</div>
				<div class="desc-content" ref="featureDescRef" v-html="detail.feature_desc"></div>
			</div>
			<!-- <ElDescriptions :column="1" border class="m-b-24px">
				<ElDescriptionsItem :span="2" label="功能描述">
					<div ref="descRef" class="description-content" v-html="detail.feature_desc"></div>
				</ElDescriptionsItem>
			</ElDescriptions> -->
		</ElCol>
		<ElCol :span="24">
			<div class="desc-item">
				<div class="desc-title">难易度</div>
				<div class="desc-content">
					<span class="dots" :style="{ backgroundColor: getColor(detail.degree_of_difficulty) }"></span>
					{{ detail.degree_of_difficulty_name || "--" }}
				</div>
			</div>
			<!-- <ElDescriptions :column="1" border class="m-b-24px">
				<ElDescriptionsItem label="难易度">
					<span :style="{ color: getColor(detail.degree_of_difficulty) }">●</span>
					{{ detail.degree_of_difficulty_name || "--" }}
				</ElDescriptionsItem>
			</ElDescriptions> -->
		</ElCol>
		<ElCol :span="24">
			<div class="desc-item">
				<div class="desc-title">难易度描述</div>
				<div class="desc-content" ref="difficultyDescRef" v-html="detail.degree_of_difficulty_desc"></div>
			</div>
			<!-- <ElDescriptions :column="1" border class="m-b-24px">
				<ElDescriptionsItem :span="2" label="难易度描述">
					<div class="description-content" v-html="detail.degree_of_difficulty_desc"></div>
				</ElDescriptionsItem>
			</ElDescriptions> -->
		</ElCol>
		<ElCol :span="24">
			<div class="title">附件</div>
			<div class="files-con">
				<template v-if="detail.files && detail.files.length > 0">
					<div style="display: inline-block; vertical-align: top; width: calc(100% - 100px)">
						<div
							v-for="(item, index) in detail.files"
							:key="index"
							class="file-item flex flex-justify-between m-b-12px"
						>
							<div
								class="flex flex-1 flex-items-center"
								style="overflow: hidden"
								@click="downFile(item.fileUrl, item.fileName)"
							>
								<img
									src="@/assets/task/file-icon.png"
									style="width: 13px; height: 13px; margin-left: 17px; margin-right: 14px"
								/>
								<ElLink class="textOverflow">{{ item.fileName }}</ElLink>
							</div>
						</div>
					</div>
				</template>
				<template v-else>
					<div class="file-item flex flex-justify-between m-b-12px p-l-16px">
						<div class="flex flex-1 flex-items-center" style="overflow: hidden">无</div>
					</div>
				</template>
			</div>
		</ElCol>
		<!-- <ElCol :span="24">
			<div class="title">功能关联任务</div>
			<ElTable :data="detail.taskList" border style="width: 100%">
				<ElTableColumn prop="taskName" label="标题" width="180" />
				<ElTableColumn prop="leaderName" label="负责人" width="180" />
				<ElTableColumn prop="startTime" label="开始日期" width="180" />
				<ElTableColumn prop="endTime" label="完成日期" width="180" />
				<ElTableColumn prop="statusName" label="状态" width="180" />
				<ElTableColumn prop="priorityName" label="优先级" width="180" />
				<ElTableColumn prop="deadline" label="截止日期" width="180" />
			</ElTable>
		</ElCol> -->
		<div v-viewer="viewerOptions" v-if="previewVisible">
			<img v-for="(url, index) in previewList" :key="index" :src="url" />
		</div>
	</ElRow>
</template>

<script setup lang="ts">
	import {
		ElDescriptions,
		ElDescriptionsItem,
		ElImageViewer,
		ElCard,
		ElRate,
		ElLink,
		ElTable,
		ElTableColumn,
		ElRow,
		ElCol
	} from "element-plus";
	import { addUpdateInfo } from "@/api/optimization/suggestion";
	import BaseForm from "@/core/struct/form/base-form";
	import { useDialogStructForm } from "@/core/struct/form/use-base-form";
	import { createFeature, editFeature, selectFeatureDetail } from "@/api/project/functionList";
	import { onMounted, ref, toRaw, watch, nextTick } from "vue";
	import { downFile } from "@/core/utils";
	import "viewerjs/dist/viewer.css";
	import { api as viewerApi } from "v-viewer";
	const detail = ref<any>({});
	const props = defineProps({
		rowInfo: {
			type: Object,
			default: () => ({})
		}
	});

	// 预览控制
	const previewVisible = ref(false);
	const previewList = ref<string[]>([]); // 图片URL集合
	const currentIndex = ref(0); // 当前图片索引

	// v-viewer 选项
	const viewerOptions = ref({
		inline: false,
		viewed() {
			this.viewer.zoomTo(1);
		},
		zIndex: 999999
	});

	const featureDescRef = ref<HTMLElement>();
	const difficultyDescRef = ref<HTMLElement>();

	// 提取富文本中所有图片URL
	const updatePreviewList = () => {
		nextTick(() => {
			const featureImages = featureDescRef.value?.querySelectorAll("img") || [];
			const difficultyImages = difficultyDescRef.value?.querySelectorAll("img") || [];
			const allImages = [...featureImages, ...difficultyImages];
			previewList.value = Array.from(allImages).map((img) => img.src);
		});
	};

	// 绑定图片点击事件
	const bindImageClick = () => {
		nextTick(() => {
			const featureImages = featureDescRef.value?.querySelectorAll("img") || [];
			const difficultyImages = difficultyDescRef.value?.querySelectorAll("img") || [];
			const allImages = [...featureImages, ...difficultyImages];

			allImages.forEach((img, index) => {
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
		const res = await selectFeatureDetail({
			id: props.rowInfo.feature_id
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
	const getColor = (value: number) => {
		switch (value) {
			case 1:
				return "#409eff"; // 轻松级，绿色
			case 2:
				return "#40bfa8"; // 简单级，浅绿色
			case 3:
				return "#67c23a"; // 正常级，黄色
			case 4:
				return "#ff9d00"; // 困难级，橙色
			case 5:
				return "#f56c6c"; // 挑战级，红色
			default:
				return "#000000"; // 默认黑色
		}
	};
</script>
<style scoped lang="scss">
	.file-item {
		width: 100%;
		height: 40px;
		background: #f8f8f8;
		border-radius: 4px 4px 4px 4px;
	}
	.textOverflow {
		white-space: nowrap;
		overflow: hidden;
		text-overflow: ellipsis;
	}
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
	.desc-item {
		width: 100%;
		display: flex;
		margin-bottom: 24px;
		border-radius: 4px 4px 4px 4px;
		border: 1px solid rgba(0, 0, 0, 0.06);
		.desc-title {
			width: 110px;
			padding: 13px 16px;
			font-size: 14px;
			color: rgba(0, 0, 0, 0.65);
			line-height: 22px;
			background: #f8f8f8;
			border-right: 1px solid rgba(0, 0, 0, 0.06);
		}
		.desc-content {
			padding: 13px 16px;
			flex: 1;
			font-size: 14px;
			color: rgba(0, 0, 0, 0.85);
			line-height: 22px;
			text-align: left;
			.dots {
				margin-right: 6px;
				display: inline-block;
				width: 10px;
				height: 10px;
				border-radius: 50%;
			}
		}
		:deep(.desc-content > p) {
			margin: 0;
		}
	}
	.title {
		margin-bottom: 9px;
		font-size: 14px;
		color: rgba(0, 0, 0, 0.85);
		line-height: 22px;
	}
	.files-con {
		margin-bottom: 24px;
		font-size: 14px;
		color: rgba(0, 0, 0, 0.85);
		line-height: 22px;
	}
</style>
