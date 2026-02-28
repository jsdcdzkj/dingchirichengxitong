<template>
	<div
		class="w-full h-59px ep-b-1px flex flex-items-center flex-justify-center"
	>
		<img class="size-32px" src="../../assets/logo.png" alt="" srcset="" />
		<span
			:class="[
				'project-logo ',
				collapse ? 'size-0 m-l-0' : 'w-176px m-l-16px'
			]"
			>项目工时管理系统</span
		>
	</div>
</template>

<script lang="ts" setup>
	import { bool } from "vue-types";

	defineProps({
		collapse: bool().def(false)
	});
</script>

<style lang="scss" scoped>
	.project-logo {
		font-family:
			Alimama ShuHeiTi,
			Alimama ShuHeiTi;
		font-weight: bold;
		font-size: 22px;
		color: var(--el-logo-color);
		line-height: 22px;
		white-space: nowrap;
		overflow: hidden;
		text-overflow: ellipsis;

		display: inline-block;
	}
</style>
