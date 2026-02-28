<template>
	<ElCard header="项目总览与预警" class="custom-header">
		<div class="flex gap-10px">
			<ProjectDifficulties />
			<ProjectQuality />
		</div>
		<div class="flex gap-10px">
			<ProjectBugTop />
			<ProjectBugAnly />
		</div>
		<div class="flex">
			<ProjectProgressTime />
			<ProjectBugSuccess />
		</div>
	</ElCard>
</template>
