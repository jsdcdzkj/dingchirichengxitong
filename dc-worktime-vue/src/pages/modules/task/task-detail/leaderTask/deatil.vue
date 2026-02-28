<template>
	<ElDescriptions :column="2" label-width="120px">
		<ElDescriptionsItem label="任务提出人：">{{ detail.proposer_user_name || '--' }}</ElDescriptionsItem>
		<ElDescriptionsItem label="紧急程度：">{{ detail.urgencyName || '--' }}</ElDescriptionsItem>
		<!-- <ElDescriptionsItem label="指派人：">{{ detail.assigned_user_name || '--' }}</ElDescriptionsItem> -->
		<ElDescriptionsItem label="创建人：">{{ detail.createUserName || '--' }}</ElDescriptionsItem>
		<ElDescriptionsItem label="创建时间：">{{ detail.createTime || '--' }}</ElDescriptionsItem>
		<ElDescriptionsItem :span="2" label="任务描述：">
			<div style="display:flex;background:#f8f8f8;padding:10px 16px;margin-top:10px">
				<span ref="descRef" class="desc-img" v-html="detail.description"></span>
			</div>
		</ElDescriptionsItem>
		<ElDescriptionsItem :span="2" label="任务附件：">
			<div style="display:inline-block;vertical-align: top; width: calc(100% - 100px)">
				<div v-for="(item,index) in detail.fileList" :key="index" class="file-item flex flex-justify-between m-b-12px">
					<div class="flex flex-1 flex-items-center" style="overflow: hidden" @click="downFile(item.fileUrl, item.fileName)">
						<img src="@/assets/task/file-icon.png" style="width: 13px;height:13px;margin-left:17px;margin-right: 14px"/>
						<ElLink class="textOverflow">{{ item.fileName }}</ElLink>
					</div>
				</div>
			</div>
			
		</ElDescriptionsItem>
		<div v-viewer="viewerOptions" v-if="previewVisible">
			<img v-for="(url, index) in previewList" :key="index" :src="url" />
		</div>
	</ElDescriptions>
</template>
<script setup lang="ts">
import {ElDescriptions, ElDescriptionsItem, ElLink } from "element-plus"
import { downFile } from "@/core/utils";
import { ref, watch, nextTick, onMounted } from "vue";
import "viewerjs/dist/viewer.css";
import { api as viewerApi } from "v-viewer";

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
.textOverflow {
	white-space: nowrap;
	overflow:hidden;
	text-overflow: ellipsis;
}

</style>