<template>
	<div class="flex-1">
		<Title title="组内BUG占比">
			<template #right>
				<ElSelect
					@change="handleChange"
					placeholder="请选择"
					style="width: 100px"
					v-model:model-value="groupId"
				>
					<ElOption v-for="item in props.groupList" :key="item.id" :label="item.name" :value="item.id" />
				</ElSelect>
			</template>
		</Title>
		<PieBugAnly class="m-t-10px" v-if="showRef" :data="data" />
	</div>
</template>

<script lang="ts" setup>
	import { bugCount, groupList } from "@/api/project/info";
	import { QuestionFilled } from "@element-plus/icons-vue";
	import { appInstance } from "@/main";
	import { ref } from "vue";
	import { array } from "vue-types";
	const data = ref([]);
	const showRef = ref(false);

	const groupId = ref();

	const props = defineProps({
		groupList: array<any>().def([])
	});

	const handleChange = () => {
		bugCount({ pro_project_id: appInstance.projectItem.id, groupId: groupId.value }).then((res) => {
			data.value = res
				.filter((item) => Number(item.count) > 0)
				.map((item: any) => {
					return {
						name: item.realName,
						value: item.count
					};
				});
		});
	};

	groupList().then((res) => {
		groupId.value = props.groupList[0].id;
		bugCount({ pro_project_id: appInstance.projectItem.id, groupId: groupId.value }).then((res) => {
			data.value = res
				.filter((item) => Number(item.count) > 0)
				.map((item: any) => {
					return {
						name: item.realName,
						value: item.count
					};
				});
			showRef.value = true;
		});
	});
</script>

<style scoped>
	h1 {
		color: #42b983;
	}
</style>
