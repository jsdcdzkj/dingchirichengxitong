<template>
	<div>
		<div class="title m-b-24px">任务指派</div>
		<ElRow :gutter="16" class="m-t-16px">
			<ElCol :span="24">
				<ElDescriptions :column="2">
					<ElDescriptionsItem :span="2" label="任务标题：">
						{{ detailInfo.title }}
					</ElDescriptionsItem>
					<ElDescriptionsItem label="截止日期：">
						{{ detailInfo.end_time }}
					</ElDescriptionsItem>
					<ElDescriptionsItem label="任务提出人：">
						{{ detailInfo.proposer_user_name }}
					</ElDescriptionsItem>
					<ElDescriptionsItem label="预估工时：">
						{{ detailInfo.plan_hours }}
					</ElDescriptionsItem>
					<ElDescriptionsItem label="紧急程度：">
						{{ detailInfo.urgencyName }}
					</ElDescriptionsItem>
					<ElDescriptionsItem :span="2" label="任务附件：">
						<div style="display:inline-block;vertical-align: top; width: calc(100% - 100px)">
							<div v-for="(item,index) in detailInfo.fileList" :key="index" class="file-item flex flex-justify-between m-b-12px">
								<div class="flex flex-1 flex-items-center" style="overflow: hidden" @click="downFile(item.fileUrl,item.fileName)">
									<img src="@/assets/task/file-icon.png" style="width: 13px;height:13px;margin-left:17px;margin-right: 14px"/>
									<ElLink class="textOverflow">{{ item.fileName }}</ElLink>
								</div>
							</div>
						</div>
						
					</ElDescriptionsItem>
					<ElDescriptionsItem :span="2" label="任务描述：">
						<div style="display:block;vertical-align: top;background:#f8f8f8;padding:10px 16px;margin-top:10px">
							<span class="desc-img" v-html="detailInfo.description"></span>
						</div>
					</ElDescriptionsItem>
				</ElDescriptions>
			</ElCol>
		</ElRow>
		<BaseForm v-bind="form" class="m-t-12px"></BaseForm>
	</div>

</template>

<script setup lang="ts">
	import { getUserList } from "@/api/common/login";
	import { saveOrUpdate, getEntityLeader } from "@/api/task/leaders-assigned";
	import { processFilesStruct } from "@/api/uploader";
	import BaseForm from "@/core/struct/form/base-form";
	import { useDialogStructForm } from "@/core/struct/form/use-base-form";
	import { ElDescriptions, ElDescriptionsItem, ElRate, ElLink } from "element-plus";
	import { toRaw, ref } from "vue";
	import { number } from "vue-types";
	import { downFile } from "@/core/utils";

	const props = defineProps({
		id: number().def(),
		row: {
			type: Object,
			default: () => ({})
		}
	});

	const { form, registerFormDone } = useDialogStructForm({
		expand: false,
		labelWidth: 88,
		showMessage: true,
		inlineMessage: false,
		labelPosition: "left",
		formItems: [
			{
				label: "指派至",
				value: 1,
				type: "ElSelect",
				prop: "assigned_to_user_id",
				span: 24,
				attrs: {
					filterable: true,
				},
				select: {
					type: "ElOption",
					label: "username",
					value: "id",
					listApi: async () => {
						const data = await getUserList({
							pageNo: 1,
							pageSize: 100,
						});
						return data.records;
					},
					list: []
				}
			},
		],
		rules: {
			assigned_to_user_id: [
				{
					required: true,
					message: "请输入",
					trigger: "change"
				}
			]
		}
	});

	registerFormDone(async () => {
		await saveOrUpdate({
				assigned_to_user_id: form.value.assigned_to_user_id,
				id: props.row.id,
				status: 2,
			})
		return true;
	});
	const detailInfo = ref({})
	if (props.row.id) {
		getEntityLeader(props.row.id).then((res) => {
			detailInfo.value = res
		});
	}
</script>
<style lang="scss" scoped>
	.title {
		width: 100%;
		height: 24px;
		line-height: 24px;
		background: linear-gradient(
			90deg,
			rgba(27, 134, 255, 0.1) 0%,
			rgba(27, 134, 255, 0) 100%
		);
		border-radius: 0px 0px 0px 0px;
		border-left: 4px solid #1b86ff;
		font-size: 16px;
		font-weight: bold;
		padding-left: 10px;
	}

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
