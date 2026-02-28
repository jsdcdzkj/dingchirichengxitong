<template>
	<div class="flex-1">
		<Title title="项目难度评估">
			<template #right>
				<ElTag
					:type="
						level === '轻松'
							? 'success'
							: level === '简单'
								? 'info'
								: level === '正常'
									? 'warning'
									: 'danger'
					"
				>
					{{ level }}
				</ElTag>
			</template>
			<!-- <template #titleOper>
				<ElPopover placement="top" width="320" trigger="hover">
					<template #reference>
						<ElIcon class="m-l-10px"><QuestionFilled /></ElIcon>
					</template>
					<template #default>
						<div style="white-space: npwrap">以功能点数最大的难度等级作为项目的难度等级</div>
					</template>
				</ElPopover>
			</template> -->
		</Title>
		<RabarDiff :data="data" v-if="showRef" />
	</div>
</template>

<script lang="ts" setup>
	import { degreeOfDifficulty } from "@/api/project/info";
	import { appInstance } from "@/main";
	import { reactive, ref } from "vue";
	const data = reactive({
		doneTaskSum: 0,
		num1: 0,
		taskSum: 0,
		doingTaskSum: 0,
		num5: 0,
		num4: 0,
		unTaskSum: 0,
		num3: 0,
		num2: 0
	});
	const list = ["轻松", "简单", "正常", "困难", "挑战"];
	const level = ref("");
	const showRef = ref(false);
	degreeOfDifficulty({ pro_project_id: appInstance.projectItem.id }).then((res) => {
		Object.keys(data).forEach((key) => {
			Reflect.set(data, key, res[key]);
		});
		showRef.value = true;
		const max = Math.max(...[data.num5, data.num4, data.num3, data.num2, data.num1]);
		level.value = list[[data.num5, data.num4, data.num3, data.num2, data.num1].indexOf(max)];
	});
</script>
