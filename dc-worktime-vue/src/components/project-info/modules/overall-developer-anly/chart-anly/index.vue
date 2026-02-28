<template>
	<div class="flex-1">
		<Title title="贡献度图表分析">
			<template #right>
				<ElRadioGroup v-model="radio1" @change="handleChangeType">
					<ElRadioButton :label="1">加班时长</ElRadioButton>
					<ElRadioButton :label="2">功能难度</ElRadioButton>
				</ElRadioGroup>
			</template>
		</Title>
		<!-- <Tabs :tabs="groupList" v-if="radio1 == 2" v-model:value="tabIndex" @change="handleTabChange">
			<template #right>
				<div class="flex gap-6px justify-end">
					<ElTag>轻松</ElTag><ElTag>简单</ElTag> <ElTag>正常</ElTag> <ElTag>困难</ElTag>
					<ElTag>挑战</ElTag>
				</div>
			</template>
		</Tabs> -->
		<div class="flex items-center justify-between mt-10px" v-if="radio1 == 2">
			<ElSelect @change="handleTabChange" placeholder="请选择" style="width: 200px" v-model:model-value="groupId">
				<ElOption v-for="item in props.groupList" :key="item.id" :label="item.name" :value="item.id" />
			</ElSelect>
			<div class="flex gap-6px justify-end">
				<el-button size="small">
					<div class="flex items-center gap-4px">
						<div w-8px h="8px" class="border-rd-8px bg-blue-500"></div>
						轻松
					</div> </el-button
				><el-button size="small"
					><div class="flex items-center gap-4px">
						<div w-8px h="8px" class="border-rd-8px bg-#40BFA8"></div>
						简单
					</div>
				</el-button>
				<el-button size="small"
					><div class="flex items-center gap-4px">
						<div w-8px h="8px" class="border-rd-8px bg-#67C23A"></div>
						正常
					</div>
				</el-button>
				<el-button size="small"
					><div class="flex items-center gap-4px">
						<div w-8px h="8px" class="border-rd-8px bg-#FF9D00"></div>
						困难
					</div>
				</el-button>
				<el-button size="small"
					><div class="flex items-center gap-4px">
						<div w-8px h="8px" class="border-rd-8px bg-#DE6E6A"></div>
						挑战
					</div>
				</el-button>
			</div>
		</div>

		<BarChartAnly v-if="radio1 == 1 && show" :xList="barAnyData.xList" :dataList="barAnyData.dataList" />
		<BarDuiditeAnly
			v-else-if="radio1 == 2 && show"
			:xList="barDuiDie.xList"
			:dataList="barDuiDie.dataList"
		></BarDuiditeAnly>
	</div>
</template>

<script lang="ts" setup>
	import { degreeOfDifficultyBug, overtimeWork } from "@/api/project/info";
	import { appInstance } from "@/main";
	import { reactive, ref } from "vue";
	import { array } from "vue-types";

	// 使用 ref 创建响应式数据
	const radio1 = ref<number>(1);
	const barAnyData = reactive({
		xList: [],
		dataList: []
	});
	const props = defineProps({
		groupList: array<any>().def([])
	});
	const show = ref(false);
	const groupId = ref(props.groupList[0].id);
	const barDuiDie = reactive({
		xList: [],
		dataList: [] as any[]
	});

	const handleTabChange = (e: any): void => {
		degreeOfDifficultyBug({ projectId: appInstance.projectItem.id, groupId: e }).then((res) => {
			barDuiDie.xList = res.map((item: any) => item.name);
			barDuiDie.dataList = [
				{ name: "轻松", data: res.map((item: any) => item["轻松"]).map(Number) },
				{ name: "简单", data: res.map((item: any) => item["简单"]).map(Number) },
				{ name: "正常", data: res.map((item: any) => item["正常"]).map(Number) },
				{ name: "困难", data: res.map((item: any) => item["困难"]).map(Number) },
				{ name: "挑战", data: res.map((item: any) => item["挑战"]).map(Number) }
			];
		});
	};

	// 定义一个 increment 函数来增加 count 的值
	const handleChangeType = (e: any): void => {
		if (e == 1) {
			overtimeWork({ projectId: appInstance.projectItem.id }).then((res) => {
				barAnyData.xList = res.map((item: any) => item.realName);
				barAnyData.dataList = res.map((item: any) => item.count);
			});
		} else {
			degreeOfDifficultyBug({ projectId: appInstance.projectItem.id, groupId: groupId.value }).then((res) => {
				barDuiDie.xList = res.map((item: any) => item.name);
				barDuiDie.dataList = [
					{ name: "轻松", data: res.map((item: any) => item["轻松"]).map(Number) },
					{ name: "简单", data: res.map((item: any) => item["简单"]).map(Number) },
					{ name: "正常", data: res.map((item: any) => item["正常"]).map(Number) },
					{ name: "困难", data: res.map((item: any) => item["困难"]).map(Number) },
					{ name: "挑战", data: res.map((item: any) => item["挑战"]).map(Number) }
				];
			});
		}
	};

	overtimeWork({ projectId: appInstance.projectItem.id }).then((res) => {
		barAnyData.xList = res.map((item: any) => item.realName);
		barAnyData.dataList = res.map((item: any) => item.count);
		show.value = true;
	});
</script>

<style scoped>
	h1 {
		color: #42b983;
	}
</style>
