<template>
	<div class="bug-table">
		<div class="bug-table-tabs">
			<div class="bug-table-tab_item" :class="{ active: active === 1 }" @click="changeActive(1)">前端</div>
			<div class="bug-table-tab_item" @click="changeActive(2)" :class="{ active: active == 2 }">后端</div>
		</div>
		<div class="bug-table-content" v-if="data.length > 0">
			<div class="bug-table-content-item w-full" v-for="(item, index) in data" :key="index">
				<div class="bug-table-content-item-span flex-1">
					<div class="dot" :class="{ active: index <= 2 }">
						{{ index + 1 }}
					</div>
					<span class="bug-table-content-item-span-text">【{{ item.name }}】</span>
				</div>
				<div
					class="bug-table-content-item-span"
					style="text-align: right; font-size: 14px; color: #344e6b; font-weight: bold"
				>
					{{ item.score }}分
				</div>
			</div>
		</div>
		<ElEmpty v-else />
	</div>
</template>

<script setup lang="ts">
	import { ref, watch } from "vue";
	import { array } from "vue-types";

	const props = defineProps({
		value: {
			type: Number,
			default: 1
		},
		data: array().def([])
	});
	const active = ref(1);
	const emit = defineEmits(["change", "update:value"]);
	const changeActive = (index: number) => {
		active.value = index;
		emit("update:value", index);
		emit("change", index);
	};

	watch(
		() => props.value,
		(newVal) => {
			active.value = newVal;
		},
		{ immediate: true }
	);
</script>

<style lang="scss" scoped>
	.bug-table {
		height: 370px;
		background: linear-gradient(180deg, #ffffff 51%, #f2f8ff 100%);
		box-shadow: 0px 0px 4px 0px rgba(46, 119, 221, 0.25);
		border-radius: 12px 12px 12px 12px;

		border-image: linear-gradient(180deg, rgba(255, 255, 255, 1), rgba(255, 255, 255, 0)) 1 1;
		overflow: hidden;
		.bug-table-tabs {
			display: flex;
			.bug-table-tab_item {
				flex: 1;
				height: 32px;
				line-height: 32px;
				text-align: center;
				font-size: 14px;
				color: #aaa;
				cursor: pointer;

				&.active {
					width: 115px;
					height: 32px;
					background: linear-gradient(180deg, #1b86ff 0%, #1b86ff 100%);
					border-radius: 0px 12px 0px 0px;
					position: relative;
					color: #fff;
					// 倒三角
					&::after {
						content: "";
						position: absolute;
						width: 0;
						height: 0;
						border-width: 6px;
						border-style: solid;
						border-color: #1b86ff transparent transparent transparent;
						bottom: -12px;
						left: 50%;
						margin-left: -6px;
					}
				}
			}
		}
		.bug-table-content {
			padding: 0 12px;
			height: 320px;
			overflow-y: auto;
			overflow-x: hidden;
			.bug-table-content-item {
				display: flex;
				height: 48px;
				border-radius: 0px 0px 0px 0px;
				border-bottom: 1px dashed rgba(0, 0, 0, 0.25);

				.bug-table-content-item-span {
					font-size: 14px;
					color: #333;
					display: flex;
					align-items: center;
					.dot {
						width: 16px;
						font-size: 12px;
						line-height: 16px;
						color: #3099fa;
						height: 16px;
						border-radius: 8px;
						background: #cbe5fe;
						&.active {
							color: #fff;
							background: linear-gradient(90deg, #30b0fa 0%, #3094fa 100%);
						}
					}
					.bug-table-content-item-span-text {
						font-weight: normal;
						font-size: 14px;
						color: #344e6b;
						font-weight: bold;
					}
				}
			}
		}
	}
</style>
