<template>
	<Title title="项目进度总览">
		<template #right>
			<ElButton type="default">未开始任务：{{ projectItem.notStartTask }}个</ElButton>
			<ElButton type="default">进行中任务：{{ projectItem.underwayTask }}个</ElButton>
			<ElButton type="default">已完成任务：{{ projectItem.finishedTask }}个</ElButton>
		</template>
	</Title>
	<div class="flex m-t-10px m-b-10px">
		<PieProgress v-bind="projectItem" v-if="!loading" />
		<div class="flex flex-col gap-12px flex-1 m-l-15px">
			<CellItem label="项目名称" :value="projectItem.projectName" />
			<CellItem label="项目参与成员" value="项目名称" v-if="projectItem.userNames">
				<template #default>
					{{
						projectItem.userNames.length > 3
							? `${projectItem.userNames.slice(0, 3).join(",")}等${projectItem.userNames.length}人`
							: projectItem.userNames.join(",")
					}}
				</template>
			</CellItem>
			<CellItem label="项目计划时间" :value="projectItem.projectStartTime && projectItem.projectEndTime ?  `${projectItem.projectStartTime}~${projectItem.projectEndTime}` :'未设定'" />
			<CellItem label="项目实际时间" :value="realTime" />
			<CellItem label="预估完成时间" :value="projectItem.expectFinish" />
		</div>
	</div>
</template>

<script setup lang="tsx">
	import Title from "@/components/project-info/common/title/index.vue";
	import { appInstance } from "@/main";
	import { computed } from "vue";
	import { getEntityById } from "@/api/project/list";
	import { reactive, ref } from "vue";

	const projectItem = reactive({
		notStartTask: 0,
		underwayTask: 0,
		finishedTask: 0,
		projectName: "",
		userNames: [],
		projectStartTime: "",
		projectEndTime: "",
		realProjectStartTime: "",
		realProjectEndTime: "",
		expectFinish: "",
		projectSchedule: 0
	});

	const loading = ref(true);

	getEntityById(appInstance.projectItem.id).then((res) => {
		Object.keys(projectItem).forEach((key) => {
			Reflect.set(projectItem, key, res[key]);
		});
		loading.value = false;
	});

	const realTime = computed(() => {
		const { realProjectStartTime, realProjectEndTime } = projectItem;
		if (realProjectStartTime && realProjectEndTime) {
			return `${realProjectStartTime}~${realProjectEndTime}`;
		} else if (realProjectStartTime) {
			return `${realProjectStartTime}~进行中`;
		}
		return "";
	});
</script>
