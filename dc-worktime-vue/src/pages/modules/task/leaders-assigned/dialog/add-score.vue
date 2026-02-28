<template>
	<div class="score-wrap">
		<div class="flex flex-items-center m-b-14px">
			<ElIcon class="m-r-8px" size="20px"><Warning color="#F56C6C"/></ElIcon>
			请领导为被指派人的工作质量结果进行评价
		</div>
		<ElDivider class="m-t-10px m-b-10px" />
		<div class="m-t-10px m-b-20px">
			<span>任务延期情况</span>
			<div class="m-t-10px m-b-16px flex flex-items-center flex-justify-between">
				<span>{{delayList[row.delay_status]}}</span>
				<span>用时评分： {{ row.delay_score }}分</span>
			</div>
		</div>
		<ElForm ref="formInstance" :model="form" labelWidth="100px" :rules="rules" label-position="top">
					<ElRow :gutter="10">
						<ElCol :span="24">
							<ElFormItem prop="quality_evaluation">
								<label slot="label" style="display: flex;justify-content: space-between;width: 100%;margin-bottom:8px">
									<div>
										<span style="color:red">*</span> 质量评价
									</div>
									质量评分：{{ form.quality_score }}分
								</label>
								<ElSelect v-model="form.quality_evaluation" @change="handleQuality" placeholder="请选择" filterable>
									<ElOption
										v-for="item in appInstance.mapDict.quality_evaluation"
										:key="item.value"
										:label="item.label"
										:value="item.value"
										></ElOption>
								</ElSelect>
							</ElFormItem>
						</ElCol>
						<ElCol :span="24">
							<ElFormItem label="评价意见" prop="evaluation_opinion">
								<ElInput type="textarea" :rows="4" v-model="form.evaluation_opinion" placeholder="请输入" clearable></ElInput>
							</ElFormItem>
						</ElCol>
					</ElRow>
		</ElForm>
		<ElDivider class="m-t-10px m-b-10px" />
		<div class="flex flex-items-center flex-justify-between">
			<span>任务总评分</span>
			<span class="grade">{{ form.task_rating }}分</span>
		</div>
	</div>
</template>

<script setup lang="ts">
	import { mark } from "@/api/task/leaders-assigned";
	import BaseForm from "@/core/struct/form/base-form";
	import { ElDivider, ElForm, ElRow, ElCol, ElInput, ElFormItem, ElSelect, ElOption } from "element-plus";
	import {
		Warning
	} from "@element-plus/icons-vue";
	import { useDialogStructForm } from "@/core/struct/form/use-base-form";
	import { useDialogForm } from "@/core/dialog/dialog-container";
	import { toRaw, ref,reactive } from "vue";
	import { number } from "vue-types";
	import { appInstance } from "@/main";

	const { registerFormDone, formInstance } = useDialogForm();

	const props = defineProps({
		id: number().def(),
		row: {
			type: Object,
			default: ({}),
		},
	});

	const delayList = ['', '提前完成', '准时完成', '超时完成']
	const rules = reactive({
		quality_evaluation: [
			{
				required: true,
				message: "请选择",
				trigger: "change"
			}
		],
		evaluation_opinion: [
			{
				required: true,
				message: "请输入",
				trigger: "blur"
			}
		]
	})

	const form = reactive({
		task_rating: props.row.delay_score + 60,
		quality_evaluation: '',
		evaluation_opinion: '',
		quality_score: 60,
	})

	const handleQuality = (val:any) => {
		console.log('3333333333333val', val)
		form.quality_score = 60 - (val - 1)*10
		form.task_rating = form.quality_score + props.row.delay_score
	}

	registerFormDone(async () => {
		console.log('2222222222222form', form)
		await mark(
			Object.assign({}, toRaw(form), {
				id: props.id
			})
		);
		return true;
	});
</script>
<style lang="scss" scoped>
.score-wrap {
	font-size: 14px;
	color: rgba(0,0,0,0.85);
	line-height: 22px;
}
.grade {
	font-size: 18px;
	line-height: 26px;
	color: #428DF7;
}
</style>