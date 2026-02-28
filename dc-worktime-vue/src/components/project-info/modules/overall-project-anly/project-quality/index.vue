<template>
	<div class="flex-1">
		<Title title="项目完成质量评估">
			<template #right>
				<ElTag v-if="data.evaluation === '差'" type="danger"> {{ data.evaluation }} </ElTag>
				<ElTag v-else-if="data.evaluation === '及格'" type="warning"> {{ data.evaluation }} </ElTag>
				<ElTag v-else-if="data.evaluation === '良好'" type="success"> {{ data.evaluation }} </ElTag>
				<ElTag v-else type="info"> {{ data.evaluation }} </ElTag>
			</template>
			<!-- <template #titleOper>
				<ElPopover placement="top" width="400" trigger="hover">
					<template #reference>
						<ElIcon class="m-l-10px"><QuestionFilled /></ElIcon>
					</template>
					<template #default>
						<div style="white-space: npwrap">
							BUG数/功能点数，10%以内为优秀，10-15为良好，15-30为及格，30以上为差
						</div>
					</template>
				</ElPopover>
			</template> -->
		</Title>
		<div class="m-t-10px">
			<div class="lenged-list">
				<div class="legend-item">项目BUG率：{{ data.source_percent }}%</div>
				<div class="legend-item">项目严重占比：{{ data.source_1_severity_3_percent }}%</div>
				<div class="legend-item">评审BUG率：{{ data.source_2_percent }}%</div>
				<div class="legend-item">评审严重占比：{{ data.source_2_severity_3_percent }}%</div>
			</div>
			<HillQuality v-if="showRef" :data="data" />
		</div>
	</div>
</template>

<script setup lang="ts">
	import { sourceSeverity } from "@/api/project/info";
	import { appInstance } from "@/main";
	import { QuestionFilled } from "@element-plus/icons-vue";
	import { reactive, ref } from "vue";

	const data = reactive({
		source_2_severity_3: 0,
		source_2_percent: 8.51,
		source_2: 4,
		source_1_severity_3_percent: 8.51,
		source_2_severity_3_percent: 0,
		source_1: 43,
		source_1_severity_3: 4,
		source_1_percent: 91.49,
		// 差，及格，良好，优秀
		evaluation: "",

		source_percent: ""
	});
	// source_1_percent , source_1_severity_3_percent source_2_severity_3_percent ,source_2_percent

	const showRef = ref(false);

	sourceSeverity({ pro_project_id: appInstance.projectItem.id }).then((res) => {
		Object.keys(data).forEach((key) => {
			Reflect.set(data, key, res[key]);
		});
		showRef.value = true;
	});
</script>

<style lang="scss" scoped>
	.lenged-list {
		display: flex;
		flex-wrap: wrap;
		gap: 10px;
		.legend-item {
			width: calc(50% - 5px);
			margin-bottom: 10px;
			background: #f4f7ff;
			border-radius: 4px 4px 4px 4px;
			border-left: 3px solid rgba(27, 134, 255, 0.64);
			padding: 5px 12px;
			font-size: 13px;
		}
	}
</style>
