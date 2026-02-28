<template>
	<div class="task-sts-tabs">
		<div
			class="task-sts-tabs__item flex"
			:class="{ active: activeIndex === index }"
			v-for="(item, index) in tabs"
			:key="index"
			@click="changeActive(index)"
		>
			<div class="task-sts-tabs__item__icon">
				<ProjectInfoIcon :name="activeIndex === index ? item.selectIcon : item.icon" :width="33" :height="33" />
			</div>

			<div class="task-sts-tabs__item__content flex flex-col justify-center items-start gap-4px">
				<span class="task-sts-tabs__item__content__count">{{ item.count }}</span>
				<span class="task-sts-tabs__item__content__title">{{ item.title }}</span>
			</div>
			<ElIcon class="task-sts-tabs__item__left-arrow" color="rgba(27, 134, 255, .9)" v-if="activeIndex === index">
				<CaretLeft />
			</ElIcon>
		</div>
		<div
			class="task-sts-tabs__select"
			:style="
				activeIndex == 0
					? `background: linear-gradient(
				180deg,
                rgba(27, 134, 255, 0.12) 0%,
				#1b86ff ${activeIndex * (100 / tabs.length)}%,
				rgba(27, 134, 255, 0.12) 100%
			);`
					: `background: linear-gradient(
				180deg,
				rgba(27, 134, 255, 0.12) 0%,
				#1b86ff ${(activeIndex + 1) * (100 / tabs.length)}%,
				rgba(27, 134, 255, 0.12) 100%
			);`
			"
		></div>
	</div>
</template>

<script setup lang="ts">
	import { ProjectInfoIcon } from "@/components/icon/icon";
	import { ref } from "vue";
	import { CaretLeft } from "@element-plus/icons-vue";
	import { appInstance } from "@/main";
	import { countDayTask } from "@/api/project/info";

	const activeIndex = ref(0);

	const tabs = ref([
		{
			title: "进行中任务",
			count: 0,
			type: 1,
			icon: "timer-flash-fill.png",
			selectIcon: "timer-flash-fill-1.png"
		},
		{
			title: "未开始任务",
			count: 0,
			type: 2,
			icon: "rest-time-fill.png",
			selectIcon: "rest-time-fill-1.png"
		},
		{
			title: "已完成任务",
			count: 0,
			type: 3,
			icon: "verified-badge-fill.png",
			selectIcon: "verified-badge-fill-1.png"
		},
		{
			title: "已挂起任务",
			count: 0,
			type: 4,
			icon: "notification-snooze-fill.png",
			selectIcon: "notification-snooze-fill-1.png"
		},
		{
			title: "已暂停任务",
			count: 0,
			type: 5,
			icon: "pause-circle-fill.png",
			selectIcon: "pause-circle-fill-1.png"
		}
	]);
	const emit = defineEmits(["changeActive"]);

	const changeActive = (index: number) => {
		activeIndex.value = index;
		emit("changeActive", tabs.value[index]);
	};
	countDayTask({ projectId: appInstance.projectItem.id }).then((res) => {
		tabs.value[0].count = res.underWay;
		tabs.value[1].count = res.notStart;
		tabs.value[2].count = res.finished;
		tabs.value[3].count = res.hangUp;
		tabs.value[4].count = res.stop;
	});
</script>

<style lang="scss" scoped>
	.task-sts-tabs {
		width: 270px;
		display: flex;
		flex-direction: column;
		gap: 9px;
		position: relative;
		&__item {
			width: 240px;
			height: 72px;
			background: linear-gradient(180deg, #ffffff 0%, #f9fcff 100%);
			border-radius: 12px 12px 12px 12px;
			border: 1px solid rgba(0, 0, 0, 0.06);
			position: relative;

			cursor: pointer;
			&__icon {
				width: 72px;
				height: 72px;
				background-color: #eaf2ff;
				display: flex;

				border-top-left-radius: 12px;
				border-bottom-left-radius: 12px;

				align-items: center;
				justify-content: center;
				&.select {
					background-color: transparent;
				}
			}
			&__content {
				flex: 1;
				height: 100%;
				padding: 0 16px;
				&.select {
					background-color: transparent;
				}
				&__count {
					font-family: "PingFang SC";
					font-style: normal;
					font-weight: 600;
					font-size: 24px;
					line-height: 28px;
					color: #275798;
				}
				&__title {
					font-family: "PingFang SC";
					font-style: normal;
					font-weight: 400;
					color: rgba(0, 0, 0, 0.65);
					font-size: 14px;
				}
			}
			&__left-arrow {
				position: absolute;
				right: -53px;

				width: 72px;
				height: 72px;
				display: flex;
				align-items: center;
				justify-content: center;
			}
			&.active {
				background-image: url(/icon/project-info/icon-item-select.png);
				background-repeat: no-repeat;
				background-size: cover;
				.task-sts-tabs__item__icon {
					background-color: transparent;
				}
				.task-sts-tabs__item__content {
					background-color: transparent;
					.task-sts-tabs__item__content__count {
						color: #fff;
					}
					.task-sts-tabs__item__content__title {
						color: #fff;
					}
				}
			}
		}

		&__select {
			width: 4px;
			height: 100%;
			position: absolute;
			right: 8px;
			top: 0;
			background: linear-gradient(
				180deg,
				rgba(27, 134, 255, 0.12) 0%,
				#1b86ff 30%,
				rgba(27, 134, 255, 0.12) 100%
			);
		}
	}
</style>
