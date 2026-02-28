<template>
	<div>
		<el-carousel
			v-if="props.list.length > 0"
			indicator-position="none"
			height="550px"
			@change="handleChange"
			ref="carousel"
			:autoplay="false"
		>
			<el-carousel-item v-for="item in list" :key="item">
				<div class="carousel-list">
					<div class="carousel-item" v-for="(it, index) in item" :key="index">
						<ProjectInfoIcon
							:name="it.postponeTime > 60 ? 'icon-warning.png' : 'icon-danger.png'"
							:width="64"
							:height="64"
							:style="it.postponeTime > 60 ? `transform: scale(0.75);` : ``"
						/>
						<div class="flex-1 flex flex-col gap-10px items-start justify-center">
							<div>
								{{ it.title }}
							</div>
							<div style="color: #aaa; font-size: 14px">负责人：{{ it.personChargeName }}</div>
						</div>
						<div class="flex-1 flex flex-col gap-10px">
							<div class="flex items-center gap-6px font-size-14px">
								<ElTag>开始</ElTag>
								{{ it.startTime }}
							</div>
							<div class="flex items-center gap-6px font-size-14px">
								<ElTag>截止</ElTag>
								{{ it.endTime }}
							</div>
						</div>
						<div class="w-150px m-t-40px flex justify-end">
							<ElTag :type="it.postponeTime > 60 ? 'warning' : 'danger'" class="flex">
								<ProjectInfoIcon name="icon-alarm-fill.png" :width="16" :height="16" />
								<span>{{ it.isPostpone == 1 ? "超期" : "剩余" }}</span>
								<span>{{ it.postPoneTemp }}</span>
							</ElTag>
						</div>
						<span class="status">
							{{ it.task_attribute }}
						</span>
					</div>
				</div>
			</el-carousel-item>
		</el-carousel>
		<div class="indicator" v-if="props.list.length > 0">
			<div
				:class="[item - 1 == current ? 'indicator-circle current' : 'indicator-circle']"
				@click="handleJump(item - 1)"
				v-for="item in list.length"
				:key="item"
			></div>
		</div>
		<ElEmpty h="330px" />
	</div>
</template>

<script setup lang="ts">
	import type { CarouselInstance } from "element-plus";
	import { ref, watch } from "vue";
	import { Icon, ProjectInfoIcon } from "@/components/icon/icon";
	import { array } from "vue-types";

	const props = defineProps({
		list: array().def([])
	});

	console.log("props.list1111111111111", props.list);

	watch(
		() => props.list,
		(val) => {
			console.log("555555555555555val", val);
		}
	);

	const current = ref(0);
	const carousel = ref<CarouselInstance>();
	const handleChange = (e: number) => {
		current.value = e;
	};

	const handleJump = (item: number) => {
		carousel.value?.setActiveItem(item);
	};
</script>

<style scoped lang="scss">
	.carousel-list {
		display: flex;
		flex-direction: column;
		gap: 16px;
		.carousel-item {
			background-image: url(../../../../../../assets/info/cal-bg.png);
			height: 90px;
			background-size: 100% 100%;
			width: 100%;
			display: flex;
			align-items: center;
			padding-left: 32px;
			padding-right: 10px;
			gap: 10px;
			position: relative;
			.status {
				position: absolute;
				right: 18px;
				top: 10px;

				font-size: 14px;
				color: #fff;
			}
			:deep(.el-tag__content) {
				display: flex;
				align-items: center;
				gap: 6px;
			}
		}
	}
	.indicator {
		display: flex;
		gap: 10px;
		justify-content: center;
		align-items: center;
		.indicator-circle {
			width: 12px;
			height: 12px;
			background: #e8f3ff;
			border-radius: 24px 24px 24px 24px;
			border: 1px solid rgba(0, 0, 0, 0.06);
			cursor: pointer;
			&.current {
				width: 48px;
				height: 12px;
				background: var(--el-color-primary);
				border-radius: 24px 24px 24px 24px;
			}
		}
	}
</style>
