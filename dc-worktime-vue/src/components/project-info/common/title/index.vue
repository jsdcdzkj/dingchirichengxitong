<template>
	<div class="flex items-center justify-between w-full">
		<div class="flex items-center gap-6px">
			<ProjectInfoIcon :name="titleIcon" />
			<span class="module-title" v-if="slots.title">
				<slot name="title">
					{{ slots.title() }}
				</slot>
			</span>
			<span class="module-title" v-else>
				{{ title }}
			</span>
			<slot name="titleOper"></slot>
		</div>
		<div class="flex items-center gap-6px">
			<slot name="right" />
		</div>
	</div>
</template>

<script setup lang="tsx">
	import type { JSX } from "vue/jsx-runtime";
	import { ProjectInfoIcon } from "@/components/icon/icon";
	import { string } from "vue-types";
	defineProps({
		title: {
			type: String,
			default: "标题"
		},
		titleIcon: string().def("icon-title.png")
	});

	const slots = defineSlots<{
		titleOper: () => JSX.Element;
		title: () => JSX.Element;
		right: () => JSX.Element;
	}>();
</script>

<style lang="scss" scoped>
	.module-title {
		font-family:
			Alibaba PuHuiTi 3,
			Alibaba PuHuiTi 30;
		font-weight: bold;
		font-size: 16px;
		color: rgba(0, 0, 0, 0.85);
	}
</style>
