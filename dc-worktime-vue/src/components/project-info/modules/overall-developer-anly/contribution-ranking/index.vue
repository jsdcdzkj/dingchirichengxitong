<template>
	<div class="flex-1">
		<Title title="开发贡献排名">
			<template #titleOper>
				<ElPopover placement="top" width="400" trigger="hover">
					<template #reference>
						<ElIcon class="m-l-10px"><QuestionFilled /></ElIcon>
					</template>
					<template #default>
						<div style="white-space: npwrap">
							<span>难度系数(轻松：0.5 简单：1 ⼀般：1.5 困难：2 挑战：3) </span><br />
							<span>权重占⽐（⼯作量30% 效率20% 难度30% bug率20%）</span><br />
							<span>⼯作量(w) = Σ(实际⼯时 * 难度系数） </span><br />
							<span
								>平均任务效率(e) =Σ(任务计划⼯时/实际⼯时）/ 任务数
								（⽐值⼤于异常指数1.5排除该任务）</span
							><br />
							<span>功能点平均复杂度(c） =Σ(功能点*难度系数）/ 功能点个数 </span><br />
							<span>bug率(b) = 功能点*难度系数/bug数 （bug率⼩于10%不扣分）</span><br />
							<span>max代表所有⼈中的最⼤值 </span><br />
							<span
								>个⼈得分 = （w/w(max) * 30% + e/e(max) * 20% + c/c(max) * 30%+b/b(max)*20%）*100</span
							>
						</div>
					</template>
				</ElPopover>
			</template>
		</Title>
		<BugTable class="m-t-10px" :data="tableList" v-model:value="typeRef" @change="handleTypeChange" />
	</div>
</template>

<script lang="ts" setup>
	import { contributionDegree } from "@/api/project/info";
	import { QuestionFilled } from "@element-plus/icons-vue";
	import { appInstance } from "@/main";
	import { ref } from "vue";

	// 使用 ref 创建响应式数据
	const title = ref<string>("Vue 3 + TypeScript");
	const count = ref<number>(0);

	// 定义一个 increment 函数来增加 count 的值
	const increment = (): void => {
		count.value++;
	};
	const typeRef = ref(1);
	const tableList = ref([]);
	const handleTypeChange = () => {
		tableList.value = [];
		contributionDegree({ type: typeRef.value, projectId: appInstance.projectItem.id }).then((res) => {
			tableList.value = res;
		});
	};

	contributionDegree({ type: typeRef.value, projectId: appInstance.projectItem.id }).then((res) => {
		// console.log(res);
		tableList.value = res;
	});
</script>

<style scoped>
	h1 {
		color: #42b983;
	}
</style>
