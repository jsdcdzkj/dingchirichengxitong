<template>
	<!-- <div class="main-container flex h-full w-full">
		<BaseSide />
		<div w="full" class="flex-1 overflow-hidden">
			<BaseHeader />
			<div w="full" class="body-container"> -->
	<ElConfigProvider :locale="zhCn"> <RouterView /></ElConfigProvider>

	<!-- </div>
		</div>
	</div> -->
</template>

<script setup lang="ts">
	import zhCn from "element-plus/es/locale/lang/zh-cn";
	import { onBeforeUnmount, onUnmounted } from "vue";
	import { appInstance } from "./main";

	onBeforeUnmount(() => {
		appInstance.destroy();
	});
</script>

<style>
	#app {
		text-align: center;
		color: var(--el-text-color-primary);
	}

	.main-container {
		height: 100vh;
	}
	.body-container {
		height: calc(100vh - var(--el-menu-item-height) - 4px);

		overflow-y: auto;
		padding: var(--body-padding-size);
		padding-top: 8px;
		background-color: var(--el-body-bg);
	}
</style>
