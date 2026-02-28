<template>
	<div class="ppp-wu">
		<div class="flex flex-items-center gap-10px">
			<img w="56px" h="48px" src="../../../../assets/milestone/icon-wenjianjia.png" alt="" srcset="" />
			<div class="flex flex-col gap-2px flex-items-start">
				<span class="title">交付物</span>
				<span class="desc">已提交{{ submitNumber }}项，共{{ pageData.dataSource.length }}项</span>
			</div>
		</div>
		<ElButton :icon="Setting" type="primary" @click="handleToWu()">设置目标交付物</ElButton>
	</div>
	<ElTable border :data="pageData.dataSource">
		<!-- <ElTableColumn type="selection" width="55" align="center">
		</ElTableColumn> -->
		<ElTableColumn label="交付物目标名称" prop="goalName" align="center"> </ElTableColumn>
		<ElTableColumn label="类型" prop="type" align="center" width="100px">
			<template #default="scope">
				{{ scope.row.type === 1 ? "链接" : "文件" }}
			</template>
		</ElTableColumn>
		<ElTableColumn label="交付情况" align="center">
			<template #default="scope">
				<template v-if="scope.row.type == 1">
					<span v-if="!scope.row.deliverableSituation || scope.row.deliverableSituation == '未交付'"
						>未交付</span
					>
					<ElLink v-else type="primary" :href="addProtocol(scope.row.deliverableSituation)" target="_blank">
						{{ scope.row.deliverableSituation }}</ElLink
					>
				</template>
				<template v-else-if="scope.row.type == 2">
					<ElLink
						v-if="scope.row.file"
						type="primary"
						@click="downFile(scope.row.file.fileUrl, scope.row.file.fileName)"
						>{{ scope.row.file.fileName }}</ElLink
					>
					<span v-else>未交付</span>
				</template>
			</template>
		</ElTableColumn>
		<ElTableColumn label="文件大小" align="center">
			<template #default="scope">
				<template v-if="scope.row.type == 2 && scope.row.file">
					{{ scope.row.file.fileSize }}
				</template>
				<span v-else>--</span>
			</template>
		</ElTableColumn>
		<ElTableColumn label="交付人" width="100px" prop="payerName" align="center"> </ElTableColumn>
		<ElTableColumn label="操作" align="center" fixed="right" width="300px">
			<template #default="scope">
				<ElButton size="small" @click="handleToWu(scope.row)">编辑目标交付物</ElButton>
				<ElButton type="primary" size="small" @click="handleEditLink(scope.row)" v-if="scope.row.type == 2"
					>上传文件</ElButton
				>
				<ElButton
					type="primary"
					size="small"
					@click="handleEditLink(scope.row)"
					v-else-if="scope.row.type == 1 && scope.row.deliverableSituation"
					>编辑链接</ElButton
				>
				<ElButton type="primary" size="small" v-else-if="scope.row.type == 1" @click="handleEditLink(scope.row)"
					>上传链接</ElButton
				>
				<ElButton type="danger" size="small" @click="handleDelete(scope.row)">删除</ElButton>
			</template>
		</ElTableColumn>
	</ElTable>
</template>

<script lang="tsx" setup>
	import { createModelAsync } from "@/core/dialog";
	import { Setting } from "@element-plus/icons-vue";
	import { computed, reactive } from "vue";
	import AddToWu from "./dialog/add-to-wu.vue";
	import { number, oneOfType, string, bool } from "vue-types";
	import { selectList } from "@/api/project/deliverables";
	import { appInstance } from "@/main";
	import { ElMessageBox, type Action, ElTable, ElTableColumn, ElButton, ElLink } from "element-plus";
	import { deleteById } from "@/api/project/deliverables";
	import AddLinkFile from "./dialog/add-link-file.vue";
	import { downFile } from "@/core/utils";
	const props = defineProps({
		milestoneId: oneOfType([number(), string()]).def(),
		dialog: bool().def(false)
	});
	const pageData = reactive({
		dataSource: [],
		activeName: "first"
	});

	const submitNumber = computed(() => {
		return pageData.dataSource.reduce((num: number, item: any) => {
			if (item.type == 1 && item.deliverableSituation) {
				num += 1;
			} else if (item.type == 2 && item.file) {
				num += 1;
			}
			return num;
		}, 0);
	});

	const handleToWu = (row?: any) => {
		let isWu = false;
		if (row?.type == 1) {
			isWu = true;
		} else if (row?.type == 2 && (!row.deliverableSituation || row.deliverableSituation == "未交付")) {
			isWu = true;
		}

		const propsWu = {
			milestoneId: props.milestoneId,
			id: row ? row.id : undefined,
			deliverables: [
				{
					goalName: row?.goalName,
					type: row?.type
				}
			],
			isWu
		};
		createModelAsync(
			{ title: "设置目标交付物", width: "1000px", closeOnClickModal: false, closeOnPressEscape: false },
			{},
			<AddToWu {...propsWu} />
		).then(() => {
			refreshTable();
		});
	};
	const refreshTable = () => {
		selectList({
			projectId: appInstance.projectItem.id,
			milestoneId: props.milestoneId
		}).then((data) => {
			pageData.dataSource = data;
		});
	};
	const handleDelete = (row: any) => {
		ElMessageBox.alert("是否删除该条数据", "提示", {
			confirmButtonText: "确认",
			callback: (action: Action) => {
				if (action == "confirm") {
					deleteById({ id: row.id }).then(() => {
						refreshTable();
					});
				}
			}
		});
	};

	const handleEditLink = (row: any) => {
		let title = "";
		let props = {};
		if (row.type == 2) {
			title = "上传文件";
		} else {
			if (row.type == 1 && row.deliverableSituation) {
				props = {
					deliverableSituation: row.deliverableSituation == "未交付" ? "" : row.deliverableSituation
				};
				title = "编辑链接";
			} else {
				title = "添加链接";
				props = {
					deliverableSituation: ""
				};
			}
		}
		createModelAsync(
			{ title, closeOnClickModal: false, closeOnPressEscape: false },
			{},
			<AddLinkFile id={row.id} type={row.type} {...props} />
		).then(() => {
			refreshTable();
		});
	};
	refreshTable();
	const addProtocol = (url) => {
		if (!/^(https?:\/\/)/i.test(url)) {
			return `https://${url}`;
		}
		return url;
	};
</script>

<style lang="scss" scoped>
	.ppp-wu {
		height: 82px;
		background: var(--el-body-bg);
		border-radius: 8px 8px 8px 8px;
		display: flex;
		align-items: center;
		padding: 0 24px;
		justify-content: space-between;
		margin-bottom: 10px;
		.title {
			font-family:
				Alibaba PuHuiTi 3,
				Alibaba PuHuiTi 30;
			font-weight: normal;
			font-size: 16px;

			text-align: center;
			font-style: normal;
			text-transform: none;
		}
		.desc {
			font-family:
				Alibaba PuHuiTi 3,
				Alibaba PuHuiTi 30;
			font-weight: normal;
			font-size: 14px;

			text-align: center;
			font-style: normal;
			text-transform: none;
		}
	}
</style>
