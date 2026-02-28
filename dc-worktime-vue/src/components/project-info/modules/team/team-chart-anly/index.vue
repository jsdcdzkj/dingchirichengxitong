<template>
	<div class="flex-1">
		<Title title="图表分析">
			<template #right>
				<ElRadioGroup v-model="radio1" @change="handleChangeType">
					<ElRadioButton :label="1">加班时长</ElRadioButton>
					<ElRadioButton :label="2">功能难度</ElRadioButton>
				</ElRadioGroup>
			</template>
		</Title>
		<div class="flex items-center justify-between mt-10px" v-if="radio1 == 2">
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
		<div>
			<BarChartAnly
				v-if="radio1 == 1 && show"
				:xList="barAnyData.xList"
				:dataList="barAnyData.dataList"
				style="height: 324px"
			/>
			<BarDuiditeAnly
				v-else-if="radio1 == 2 && show"
				:xList="barDuiDie.xList"
				:dataList="barDuiDie.dataList"
				style="height: 290px"
			></BarDuiditeAnly>
		</div>
	</div>
</template>

<script lang="ts" setup>
	import { getOvertimeWork, getDifficulty } from "@/api/task/group";
	import { reactive, ref, watch } from "vue";
	import { array } from "vue-types";

	const props = defineProps({
		groupId: Number,
		projectId: Number
	});
	const show = ref(false);

	const barDuiDie = reactive({
		xList: [],
		dataList: [] as any[]
	});
	const barAnyData = reactive({
		xList: [],
		dataList: []
	});
	const radio1 = ref<number>(1);

	// 定义一个 increment 函数来增加 count 的值
	const handleChangeType = (e: any): void => {
		if (e == 1) {
			getOvertimeWork({
				projectId: props.projectId !== 0 ? props.projectId : undefined,
				groupId: props.groupId
			}).then((res) => {
				barAnyData.xList = res.map((item: any) => item.realName);
				barAnyData.dataList = res.map((item: any) => item.count);
			});
		} else {
			getDifficulty({
				projectId: props.projectId !== 0 ? props.projectId : undefined,
				groupId: props.groupId
			}).then((res) => {
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

	const fetchData = async () => {
		// 如果projectId值为0，则给projectId赋值为null
		if (radio1.value === 1) {
			getOvertimeWork({
				projectId: props.projectId !== 0 ? props.projectId : undefined,
				groupId: props.groupId
			}).then((res) => {
				barAnyData.xList = res.map((item: any) => item.realName);
				barAnyData.dataList = res.map((item: any) => item.count);
			});
		} else if (radio1.value === 2) {
			// 当 radio1 为 2 时，请求功能难度数据
			getDifficulty({
				projectId: props.projectId !== 0 ? props.projectId : undefined,
				groupId: props.groupId
			}).then((res) => {
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

	// 监听 groupId 和 projectId 变化
	watch(
		[() => props.groupId, () => props.projectId],
		async () => {
			await fetchData();
		},
		{ deep: true }
	);

	// 组件挂载时初始化数据
	fetchData();

	getOvertimeWork({ projectId: props.projectId !== 0 ? props.projectId : undefined, groupId: props.groupId }).then(
		(res) => {
			barAnyData.xList = res.map((item: any) => item.realName);
			barAnyData.dataList = res.map((item: any) => item.count);
			show.value = true;
		}
	);
</script>

<style scoped>
	h1 {
		color: #42b983;
	}
</style>
