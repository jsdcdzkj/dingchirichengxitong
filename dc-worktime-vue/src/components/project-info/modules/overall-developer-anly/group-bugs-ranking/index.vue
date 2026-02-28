<template>
	<div class="flex-1">
		<Title title="综合BUG指数排名">
			<template #titleOper>
				<ElPopover placement="top" width="400" trigger="hover">
					<template #reference>
						<ElIcon class="m-l-10px"><QuestionFilled /></ElIcon>
					</template>
					<template #default>
						<div style="white-space: npwrap">
							<span>难度系数(轻松：0.5 简单：1 ⼀般：1.5 困难：2 挑战：3)</span><br />
							<span>严重程度(轻微：1 ⼀般：1.5 严重：2)</span><br />
							<span>bug指数 = Σ(1/难度系数*bug严重程度)</span>
						</div>
					</template>
				</ElPopover>
			</template>
		</Title>
		<BugTable class="m-t-10px" :data="tableList" v-model:value="typeRef" @change="handleTypeChange" />
	</div>
</template>

<script lang="ts" setup>
	import { bugIndex } from "@/api/project/info";
	import { QuestionFilled } from "@element-plus/icons-vue";
	import { appInstance } from "@/main";
	import { ref } from "vue";

	const typeRef = ref(1);
	const tableList = ref([]);
	const handleTypeChange = () => {
		tableList.value = [];
		bugIndex({ type: typeRef.value, projectId: appInstance.projectItem.id }).then((res) => {
			tableList.value = res;
		});
	};

	bugIndex({ type: typeRef.value, projectId: appInstance.projectItem.id }).then((res) => {
		tableList.value = res;
	});
</script>

<style scoped>
	h1 {
		color: #42b983;
	}
</style>
