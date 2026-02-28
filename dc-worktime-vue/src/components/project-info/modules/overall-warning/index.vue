<template>
	<ElCard header="项目总览与预警" class="custom-header">
		<OverAll />
		<Warning />
	</ElCard>
</template>

<script setup lang="tsx">
	import OverAll from "./overall/index.vue";
	import Warning from "./warning/index.vue";
</script>
