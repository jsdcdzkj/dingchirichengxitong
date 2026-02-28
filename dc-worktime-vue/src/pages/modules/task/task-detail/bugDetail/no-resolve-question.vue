<template>
	<div class="flex" style="min-height: 200px; width: 100%">
		<div class="flex" v-if="detail.testBugHistoryList && detail.testBugHistoryList.length > 0">
			<div class="left">
				<div
					class="item"
					:class="{ active: activeIndex == index }"
					v-for="(item, index) in detail.testBugHistoryList"
					:key="index"
					@click="handleClick(index, item)"
				>
					<span class="m-l-12px">{{ item.createTime }}</span>
				</div>
			</div>
			<div class="right flex-1" v-if="noRsolveInfo">
				<ElDescriptions :column="1">
					<ElDescriptionsItem label="解决人：">{{ noRsolveInfo.create_user_name }}</ElDescriptionsItem>
					<ElDescriptionsItem :label="titleType + '完成状态：'">{{
						noRsolveInfo.statesName
					}}</ElDescriptionsItem>
					<ElDescriptionsItem label="解决说明：" class-name="my-content">
						<div ref="descRef" class="desc-img" v-html="noRsolveInfo.description"></div>
					</ElDescriptionsItem>
					<div v-viewer="viewerOptions" v-if="previewVisible">
						<img v-for="(url, index) in previewList" :key="index" :src="url" />
					</div>
					<!--<ElDescriptionsItem label="变更人：">{{ item.delay_status}}</ElDescriptionsItem>-->
				</ElDescriptions>
			</div>
		</div>
		<div v-else class="flex flex-1 flex-col flex-items-center flex-justify-center">
			<img src="@/assets/no-Data.png" style="width: 120px" />
			<span style="margin-top: 10px; color: #999">暂无数据</span>
		</div>
	</div>
</template>
<script setup lang="ts">
	import { computed, ref, watch, nextTick, onMounted } from "vue";
	import "viewerjs/dist/viewer.css";
	import { api as viewerApi } from "v-viewer";
	import { ElDescriptions, ElDescriptionsItem } from "element-plus";

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

	const noRsolveInfo = ref(props.detail.testBugHistoryList?.[0]);
	const activeIndex = ref(0);
	const handleClick = (index: any, item: any) => {
		activeIndex.value = index;
		noRsolveInfo.value = item;
		updatePreviewList();
		bindImageClick();
	};
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
	// 初始化
	onMounted(() => {
		updatePreviewList();
		bindImageClick();
		if (props.testType == "3" || props.testType == "4") {
			titleType.value = "问题";
		} else {
			titleType.value = "bug";
		}
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
<style lang="scss" scoped>
	.left {
		border-right: 1px solid rgba(0, 0, 0, 0.06);
		margin-right: 24px;
		padding: 0 24px;
		.item {
			width: 182px;
			height: 44px;
			line-height: 44px;
			background: rgba(255, 255, 255, 0);
			border-radius: 8px 8px 8px 8px;
		}
		.active {
			background: #e8f3ff;
			border-radius: 8px 8px 8px 8px;
		}
	}
	.right {
		text-align: left;
	}
	:deep(.el-descriptions__table) > tbody > tr:last-child {
		.el-descriptions__cell {
			display: flex;
		}
		.my-content {
			flex: 1;
		}
		.desc-img > p {
			margin: 0;
		}
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
