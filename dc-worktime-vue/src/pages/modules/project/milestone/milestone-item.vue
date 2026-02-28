<template>
	<div class="milestone-item">
		<img :src="`/milestone/${icon}.png`" alt="" w="48px" h="48px" srcset="" />
		<div class="flex flex-col justify-center items-center item-content">
			<span class="value">{{ value }}</span>
			<span class="description">{{ description }}</span>
		</div>
	</div>
</template>

<script lang="tsx" setup>
	import { number, string } from "vue-types";

	defineProps({
		icon: string().def(),
		value: number().def(),
		description: string().def()
	});
</script>

<style lang="scss" scoped>
	.milestone-item {
		flex: 1;
		height: 96px;
		background: var(--el-page-color);
		box-shadow: 0px 0px 7px 0px rgba(0, 0, 0, 0.05);
		border-radius: 8px 8px 8px 8px;
		border: 1px solid rgba(0, 0, 0, 0.06);
		display: flex;
		align-items: center;
		padding: 0 18px;
		gap: 15px;
		.item-content {
			align-items: flex-start;
			gap: 4px;
			.value {
				font-family:
					Douyin Sans,
					Douyin Sans;
				font-weight: bold;
				font-size: 18px;
			}
			.description {
				font-family:
					Alibaba PuHuiTi 3,
					Alibaba PuHuiTi 30;
				font-weight: normal;
				font-size: 14px;
			}
		}
	}
</style>
