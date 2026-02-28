<template>
	<div class="progress-con">
		<div class="progress-value" :style="{ width: `${progress}%` }"></div>
	</div>
</template>

<!-- <script> -->

<script setup lang="ts">
	import { number } from "vue-types";

	defineProps({
		progress: number().def(0)
	});
</script>

<style lang="scss" scoped>
	.progress-con {
		width: 100%;
		position: absolute;
		left: 0;
		bottom: 54px;
		height: 16px;
		background: #f8f8f8;
		box-shadow: inset 0px 0px 4px 0px rgba(0, 0, 0, 0.15);
		border-radius: 24px 24px 24px 24px;
		overflow: hidden;
		.progress-value {
			width: 0%;
			height: 100%;
			background-image: url(../../../../assets/info/progress-bg.png);
			border-radius: 24px 24px 24px 24px;
		}
	}
</style>
