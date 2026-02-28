<template>
	<ElDescriptions :column="3">
		<ElDescriptionsItem label="任务开始时间：">{{ detail.start_time || '--'}}</ElDescriptionsItem>
		<ElDescriptionsItem label="任务完成时间：">{{ detail.completion_time || '--'}}</ElDescriptionsItem>
		<ElDescriptionsItem label="任务延期情况：">{{ detail.delayStatusName || '--'}}</ElDescriptionsItem>
		<ElDescriptionsItem :span="2" label="工作成果：" v-if="detail.task_attribute == 4">
			<div v-if="detail.work_product" style="display:flex;background:#f8f8f8;padding:10px 16px;margin-top:10px">
				<span ref="descRef" class="desc-img" v-html="detail.work_product"></span>
			</div>
		</ElDescriptionsItem>
		<ElDescriptionsItem :span="3" label="工作成果：" v-if="detail.task_attribute != 4">
			<div style="display:inline-block; width: calc(100% - 100px);vertical-align: top;">
				<div class="links" v-for="(item,index) in detail.taskWorkResultList" :key="index">
					<Link size="13px" color="#409eff" />
					<ElLink class="m-l-8px" @click="handleOpen(item.url)">{{ item.url }}</ElLink>
				</div>
			</div>
		</ElDescriptionsItem>
		<ElDescriptionsItem :span="3" label="成果附件：" v-if="detail.task_attribute != 4">
			<div style="display:inline-block; width: calc(100% - 100px);vertical-align: top;">
				<div v-for="(item,index) in detail.fileList" :key="index" class="file-item flex flex-justify-between m-b-12px">
					<div class="flex flex-items-center" @click="downFile(item.fileUrl, item.fileName)">
						<img src="@/assets/task/file-icon.png" style="width: 13px;height:13px;;margin-right: 14px"/>
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
import { ref, onMounted, watch, nextTick } from "vue"
import { Link } from "@element-plus/icons-vue";
import {ElDescriptions, ElDescriptionsItem, ElLink } from "element-plus"
import "viewerjs/dist/viewer.css";
import { api as viewerApi } from "v-viewer";
import { downFile } from "@/core/utils";
const props = defineProps({
		detail: {
			type:Object,
			default: {}
		}
	});

const linkList = ref([])

const handleOpen = (url) => {
	window.open(url,"_blank")
}

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
<style lang="scss" scoped>
.links {
	padding: 0 16px;
	width: 100%;
	display: flex;
	align-items: center;
	height: 40px;
	background: #F8F8F8;
	border-radius: 4px 4px 4px 4px;
	margin-bottom: 8px;
}
.file-item {
	width: 100%;
	height: 40px;
	background: #F8F8F8;
	border-radius: 4px 4px 4px 4px;
	padding:0 16px;
}
.textOverflow {
	white-space: nowrap;
	overflow:hidden;
	text-overflow: ellipsis;
}
</style>
