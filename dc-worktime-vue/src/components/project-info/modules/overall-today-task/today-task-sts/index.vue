<template>
	<div class="flex-shrink-0">
		<Title title="当日任务统计" style="height: 32px" />
		<TaskStsTabs class="m-t-10px" @change-active="(e) => emit('changeActive', e)" />
	</div>
</template>

<script lang="ts" setup>
	import { ref } from "vue";

	// 使用ref定义响应式变量
	const count = ref(0);
	const title = ref("Hello Vue 3 with TypeScript!");
	const emit = defineEmits(["changeActive"]);
	// 定义一个方法来增加count的值
	const increment = () => {
		count.value++;
	};
</script>

<style scoped></style>
