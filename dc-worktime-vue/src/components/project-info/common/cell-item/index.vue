<template>
	<div class="cell-item flex w-full">
		<div class="label">{{ label }}</div>
		<div class="value flex">
			<div w="100%" h="100%" v-if="slots.default" class="flex-1 value-content">
				<slot></slot>
			</div>
			<div v-else class="flex-1 value-content">
				{{ value }}
			</div>
			<div class="value-bg"></div>
		</div>
	</div>
</template>

<script lang="ts" setup>
	defineOptions({
		name: "CellItem"
	});
	defineProps({
		label: {
			type: String,
			required: true
		},
		value: {
			type: String,
			required: true
		}
	});
	const slots = defineSlots();
</script>

<style scoped>
	.cell-item {
		display: flex;
		height: 32px;
		border-top-left-radius: 6px;
		border-bottom-left-radius: 6px;
		overflow: hidden;
	}
	.label {
		font-weight: bold;
		width: 140px;
		height: 32px;
		line-height: 32px;
		text-align: center;
		background-color: #e6f2ff;
		background-image: url(/icon/project-info/icon-cell-title.png);
		background-size: 100% 100%;
		flex-shrink: 0;
		color: #275798;
		font-size: 14px;
	}
	.value {
		color: #666;
		flex: 1;
		height: 32px;
		line-height: 32px;
		position: relative;
	}
	.value-content {
		background-color: #f4f7ff;
		font-weight: normal;
		font-size: 14px;
		color: rgba(0, 0, 0, 0.85);
		font-weight: bold;
		text-align: left;
		padding: 0 10px;
	}
	.value-bg {
		width: 50px;
		height: 32px;
		background-image: url(/icon/project-info/icon-title-prend.png);
		background-size: 100% 100%;
	}
</style>
