<template>
	<div class="w-tabs flex m-t-10px">
		<div class="w-tabs-content flex">
			<div
				class="w-tabs-item"
				v-for="(item, index) in props.tabs"
				:key="index"
				:class="{ active: activeTab === index }"
				@click="handleChangeCurrent(index, item)"
			>
				{{ item.name }}
			</div>
		</div>
		<div class="w-tabs-oper flex-1">
			<slot name="right"></slot>
		</div>
	</div>
</template>

<script setup lang="ts">
	import { ref, watch } from "vue";
	import { number } from "vue-types";

	const props = defineProps({
		tabs: {
			type: Array,
			required: true,
			default: () => [{ name: "合计工时分布", id: 1 }]
		},
		value: number().def(0)
	});

	const activeTab = ref(0);
	const emit = defineEmits(["change", "update:value"]);
	const handleChangeCurrent = (index: number, item: any) => {
		console.log("33333333333", index, item);
		emit("change", index, item);
		emit("update:value", index);
	};

	watch(
		() => props.value,
		(val) => {
			activeTab.value = val;
		},
		{ immediate: true }
	);
	defineSlots<{ right: () => any }>();
</script>

<style lang="scss" scoped>
	.w-tabs {
		height: 32px;
		border-bottom: 1px solid rgba(0, 0, 0, 0.06);

		.w-tabs-content {
			gap: 4px;
			cursor: pointer;
			.w-tabs-item {
				border-radius: 8px 8px 0px 0px;
				border: 1px solid rgba(0, 0, 0, 0.06);
				border-bottom: 0;
				height: 32px;
				line-height: 32px;
				padding: 0 10px;
				font-size: 12px;
				margin-bottom: -1px;
				background-color: #fff;
				&.active {
					background: linear-gradient(180deg, #e8f4fe 0%, #d7edff 100%);
					border-radius: 8px 8px 0px 0px;
					border-bottom: 2px solid #1e95f4;
					color: #409eff;
				}
			}
		}
		.w-tabs-oper {
		}
	}
</style>
