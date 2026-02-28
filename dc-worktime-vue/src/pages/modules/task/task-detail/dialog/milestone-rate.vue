<template>
	<div style="overflow: auto; height: 800px">
		<ElCard shadow="never" class="m-t-10px">
			<div class="flex flex-items-center">
				<img
					w="24px"
					h="24px"
					src="../../../../../assets/milestone/icon-tag.png"
					alt=""
					srcset=""
				/>
				<span class="m-l-10px">项目里程碑/阶段信息</span>
			</div>
			<div class="m-t-20px">
				<ElDescriptions :column="1">
					<ElDescriptionsItem label="项目名称："
						>里程碑</ElDescriptionsItem
					>
					<ElDescriptionsItem label="计划名称："
						>里程碑</ElDescriptionsItem
					>
					<ElDescriptionsItem label="里程碑/阶段："
						>里程碑</ElDescriptionsItem
					>
				</ElDescriptions>
			</div>
		</ElCard>
		<ElCard shadow="never" class="m-t-10px">
			<div class="flex flex-items-center">
				<img
					w="24px"
					h="24px"
					src="../../../../../assets/milestone/icon-tag.png"
					alt=""
					srcset=""
				/>
				<span class="m-l-10px">评审信息</span>
			</div>
			<div class="m-t-20px">
				<ElDescriptions :column="1">
					<ElDescriptionsItem label="计划评审时间："
						>2025-10-01</ElDescriptionsItem
					>
					<ElDescriptionsItem label="评审内容描述：">
						<ElInput type="textarea" :rows="4" />
					</ElDescriptionsItem>
					<ElDescriptionsItem label="评审内容描述："
						><ElCascader
					/></ElDescriptionsItem>
				</ElDescriptions>
			</div>
		</ElCard>
		<ElCard shadow="never" class="m-t-10px">
			<div class="flex flex-items-center flex-justify-between">
				<div class="flex flex-items-center">
					<img
						w="24px"
						h="24px"
						src="../../../../../assets/milestone/icon-tag.png"
						alt=""
						srcset=""
					/>
					<span class="m-l-10px">验收BUG记录</span>
				</div>
				<ElButton plain @click="handleAddBug">记录bug</ElButton>
			</div>
			<div class="m-t-20px">
				<ElTable
					border
					:data="data.data"
					:span-method="objectSpanMethod"
				>
					<ElTableColumn
						label="ID"
						prop="id"
						align="center"
					></ElTableColumn>
					<ElTableColumn label="板块" align="center">
						<template #default="scope"> 111 </template>
					</ElTableColumn>
					<ElTableColumn label="类型" align="center">
						<template #default="scope"> 222 </template>
					</ElTableColumn>
					<ElTableColumn label="问题描述" align="center"
						><template #default="scope">
							333
						</template></ElTableColumn
					>
					<ElTableColumn label="责任人" align="center"
						><template #default="scope">
							444
						</template></ElTableColumn
					>
				</ElTable>
			</div>
		</ElCard>
		<ElCard shadow="never" class="m-t-10px">
			<div class="flex flex-items-center">
				<img
					w="24px"
					h="24px"
					src="../../../../../assets/milestone/icon-tag.png"
					alt=""
					srcset=""
				/>
				<span class="m-l-10px">评审总结信息</span>
			</div>
			<div class="m-t-20px">
				<ElDescriptions :column="2">
					<ElDescriptionsItem label="里程碑延期天数："
						>里程碑</ElDescriptionsItem
					>
					<ElDescriptionsItem label="里程碑按期完成评分："
						>里程碑</ElDescriptionsItem
					>
					<ElDescriptionsItem label="验收重大BUG数量："
						>里程碑</ElDescriptionsItem
					>
					<ElDescriptionsItem label="验收重大BUG评分："
						>里程碑</ElDescriptionsItem
					>
					<ElDescriptionsItem label="验收一般BUG数量："
						>里程碑</ElDescriptionsItem
					>

					<ElDescriptionsItem label="验收一般BUG评分："
						>里程碑</ElDescriptionsItem
					>
					<ElDescriptionsItem label="验收轻微BUG数量："
						>里程碑</ElDescriptionsItem
					>
					<ElDescriptionsItem label="验收轻微BUG评分："
						>里程碑</ElDescriptionsItem
					>
					<ElDescriptionsItem label="评审结果："
						>里程碑</ElDescriptionsItem
					>
					<ElDescriptionsItem label="评审总分："
						>里程碑</ElDescriptionsItem
					>
					<ElDescriptionsItem label="评审意见："
						><ElInput type="textarea" :rows="4"
					/></ElDescriptionsItem>
				</ElDescriptions>
			</div>
		</ElCard>
	</div>
</template>

<script lang="tsx" setup>
	import {
		ElInput,
		ElDescriptions,
		ElCard,
		ElDescriptionsItem,
		ElCascader,
		ElTable,
		ElTableColumn,
		ElButton
	} from "element-plus";
	import { reactive } from "vue";
	import AddBug from "./add-bug.vue";
	import { createModelAsync } from "@/core/dialog";

	const data = reactive({
		data: [{ id: 1 }, { id: 2 }, { id: 3 }, { id: 4 }, { id: 5 }]
	});

	const objectSpanMethod = ({ row, column, rowIndex, columnIndex }) => {
		if (columnIndex === 1) {
			if (rowIndex % 5 == 0) {
				return {
					rowspan: 5,
					colspan: 1
				};
			} else {
				return {
					rowspan: 0,
					colspan: 0
				};
			}
		}
	};

	const handleAddBug = () => {
		createModelAsync({ title: "记录bug", width: "1000px", closeOnClickModal: false,closeOnPressEscape: false }, {}, <AddBug />);
	};
</script>
