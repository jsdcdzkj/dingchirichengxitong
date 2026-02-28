<template>
	<ElCard header="开发人员项目贡献统计分析" class="custom-header">
		<div class="flex gap-10px">
			<ContributionRanking />
			<GroupBugsProportion v-if="groupListRef.length" :groupList="groupListRef" />
			<GroupBugsRanking />
		</div>
		<ChartAnly v-if="groupListRef.length" :groupList="groupListRef" />
	</ElCard>
</template>

<script setup lang="ts">
	import { groupList } from "@/api/project/info";
	import { ref } from "vue";
	const groupListRef = ref([]);

	groupList().then((res) => {
		groupListRef.value = res;
	});
</script>
