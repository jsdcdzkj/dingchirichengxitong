<template>
	<ElDescriptions :column="2" label-width="120px">
		<ElDescriptionsItem label="关联项目名称：">{{ detail.project_name || '--' }}</ElDescriptionsItem>
		<ElDescriptionsItem label="优先级：">{{ detail.priorityName || '--' }}</ElDescriptionsItem>
		<ElDescriptionsItem label="难度等级：">{{ detail.degreeName || '--' }}</ElDescriptionsItem>
		<ElDescriptionsItem label="创建人：">{{ detail.createUserName || '--' }}</ElDescriptionsItem>
		<ElDescriptionsItem label="创建时间：">{{ detail.createTime || '--' }}</ElDescriptionsItem>
		<ElDescriptionsItem label="指派人：">{{ detail.assigned_user_name || '--' }}</ElDescriptionsItem>
		<ElDescriptionsItem :span="2" label="指派时间：">{{ detail.assign_time || '--' }}</ElDescriptionsItem>
		<ElDescriptionsItem :span="2" label="任务描述：">
			<div v-if="detail.description" style="display:flex;background:rgba(0,0,0,0.04);padding:10px 16px;margin-top:10px">
				<span ref="descRef" class="desc-img" v-html="detail.description"></span>
			</div>
		</ElDescriptionsItem>
		<div v-viewer="viewerOptions" v-if="previewVisible">
			<img v-for="(url, index) in previewList" :key="index" :src="url" />
		</div>
	</ElDescriptions>
</template>
<script setup lang="ts">
import { ref, watch, nextTick, onMounted } from "vue";
import "viewerjs/dist/viewer.css";
import { api as viewerApi } from "v-viewer";
import {ElDescriptions, ElDescriptionsItem,ElImage } from "element-plus"
const props = defineProps({
		detail: {
			type:Object,
			default: {}
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
<style scope lang="scss">
.file-item {
	width: 100%;
	height: 40px;
	background: #F8F8F8;
	border-radius: 4px 4px 4px 4px;
}


.el-descriptions__label:not(.is-bordered-label) {
	color: rgba(0,0,0,0.85);
}
.el-descriptions__content:not(.is-bordered-label) {
	color: rgba(0,0,0,0.65);
}
</style>
