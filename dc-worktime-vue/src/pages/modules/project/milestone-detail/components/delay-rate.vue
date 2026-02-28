<template>
	<div :class="['rate-rules', rateData.cls]">
		<div class="rate-rules_text">{{ levelLabels[rateData.level] }}</div>
		<div class="rate-rules_value">{{ rate }}</div>
	</div>
</template>

<script setup lang="ts">
	import { computed } from "vue";
	const props = defineProps({
		rate: {
			type: Number,
			default: 0
		}
	});
	const levelLabels = ["提前完成", "按时完成", "轻微延期", "严重延期"];

	const rateData = computed(() => {
		if (props.rate == 40) {
			return { level: 0, cls: "primary" };
		} else if (props.rate == 30) {
			return { level: 1, cls: "success" };
		} else if (props.rate == 20) {
			return { level: 2, cls: "warning" };
		} else {
			return { level: 3, cls: "danger" };
		}
	});
</script>

<style scoped lang="scss">
	.rate-rules {
		width: 88px;
		height: 24px;

		border-radius: 4px 4px 4px 4px;
		border: 1px solid #1b86ff;
		background: #e8f3ff;
		display: inline-flex;
		align-items: center;
		justify-content: space-between;

		&_text {
			font-family:
				Alibaba PuHuiTi 3,
				Alibaba PuHuiTi 30;
			font-weight: normal;
			font-size: 14px;
			color: #1b86ff;
			flex: 1;
			text-align: center;
			line-height: 22px;
		}
		&_value {
			width: 24px;
			height: 24px;
			background: #1b86ff;
			border-radius: 4px 4px 4px 4px;
			color: #fff;
			font-weight: bold;
			font-size: 12px;
			text-align: center;
			line-height: 24px;
		}
		&.success {
			border: 1px solid #67c23a;
			background: #f0f9eb;
			.rate-rules_value {
				background: #67c23a;
			}

			.rate-rules_text {
				color: #67c23a;
			}
		}

		&.danger {
			border: 1px solid #1b86ff;
			background: #e8f3ff;
			.rate-rules_value {
				background: #67c23a;
			}

			.rate-rules_text {
				color: #67c23a;
			}
		}

		&.warning {
			border: 1px solid #f5a250;
			background: #fef6ed;
			.rate-rules_value {
				background: #f5a250;
			}

			.rate-rules_text {
				color: #f5a250;
			}
		}

		&.danger {
			border: 1px solid #f56c6c;
			background: #fef0f0;
			.rate-rules_value {
				background: #f56c6c;
			}

			.rate-rules_text {
				color: #f56c6c;
			}
		}
	}
</style>
