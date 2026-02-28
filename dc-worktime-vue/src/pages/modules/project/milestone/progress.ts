import { selectMilestoneByProjectId } from "@/api/project/milestone";
import { Gesture, GestureEnum, Scroller, type GestureEvent } from "@/core/common";
import { appInstance } from "@/main";
import { nextTick, onMounted, onUnmounted, ref } from "vue";

export default class BaseScrollController extends Scroller {
	_scrollX = 0;
	boundary = [0, 0];
	pWidth = 0;
	constructor() {
		super();
	}
	setBoundary(min: number, max: number) {
		this.boundary = [min, max];
	}

	setPWidth(width: number) {
		this.pWidth = width;
	}

	setScrollX(distance: number) {
		const [min, max] = this.boundary;
		const width = this.pWidth;
		const scroll = width + distance;
		if (min == max) {
			this._scrollX = distance;
		} else {
			if (max < width) {
				return;
			}
			if (distance < min) {
				this._scrollX = Math.max(distance, min);
			} else if (scroll > max) {
				this._scrollX = max - width;
			} else {
				this._scrollX = Math.min(distance, max);
			}
		}
	}

	scroll(event: GestureEvent) {
		this.setScrollX(this.scrollx - (event.gesture.offsetX || 0));
	}

	get scrollx() {
		return this._scrollX;
	}
}

export const useProgress = (className: string) => {
	const projectMilestoneList = ref<any[]>([]);
	let obs: ResizeObserver | null = null;
	let pContainer: HTMLDivElement | null = null;
	let gesture: Gesture | null = null;
	let scroller: BaseScrollController = new BaseScrollController();

	const handleGesture = (e: any) => {
		switch (e.status) {
			case GestureEnum.move:
				scroller.setFv(e.gesture?.offsetY || 0, e.gesture?.t || 0);
				scroller.scroll(e);
				requestAnimationFrame(renderTranslateX);
				break;
		}
	};

	const renderTranslateX = () => {
		if (pContainer && pContainer.firstChild) {
			pContainer.firstChild.style.transform = `translateX(${-scroller.scrollx}px)`;
		}
		projectMilestoneList.value.forEach((item: any) => {
			item.visible = false;
		});
	};

	const refreshProjectStatus = (iterationId?: number | string | null) => {
		pContainer = document.querySelector(`.${className}`);
		console.log(iterationId);

		selectMilestoneByProjectId({
			projectId: appInstance.projectItem.id,
			iterationId
		}).then((res) => {
			if (pContainer) {
				gesture = new Gesture({
					container: pContainer,
					callback: handleGesture,
					preventDefault: true,
					stopPropagation: true
				});

				parentContainerBindObserverReize(pContainer);
				const width = computedProgressItemWidth(res.length, pContainer.offsetWidth);
				scroller.setBoundary(0, width * res.length + width);
				scroller.setPWidth(pContainer.offsetWidth);

				gesture.registerEvent();
				let preItem: any | null = null;

				projectMilestoneList.value = res.map((item: any, index: number) => {
					let status = 0; // 未开始

					if (item.state == 1 && item.proReview && Number(item.proReview.milestonePostponeDay) > 0) {
						status = 2; // 延期完成
					} else if (item.state == 1 && item.proReview && Number(item.proReview.greatNum) > 0) {
						status = 4; // 有重大bug
					} else if (item.state == 1 && item.proReview && Number(item.proReview.reviewTotalPoints) < 60) {
						status = 3; // 未通过
					} else if (item.state == 1 && item.proReview && Number(item.proReview.reviewTotalPoints) >= 60) {
						status = 1; // 已完成
					}

					// 上一个节点评审状态已完成并且当前节点为未评审
					if (preItem && preItem.completed && status == 0) {
						// 等待评审
						status = 5;
					}

					preItem = {
						...item,
						completed: item.state == 1,
						status,
						w: width,
						visible: false
					};
					return {
						...preItem
					};
				});

				nextTick(() => {
					scroller.setScrollX(width * res.length);
					renderTranslateX();
				});
			}
		});
	};

	// 监听父容器大小变化
	const parentContainerBindObserverReize = (pContainer: HTMLDivElement) => {
		obs = new ResizeObserver(() => {
			const width = computedProgressItemWidth(projectMilestoneList.value.length, pContainer.offsetWidth);
			projectMilestoneList.value = projectMilestoneList.value.map((item: any) => {
				return {
					...item,
					w: width,
					visible: false
				};
			});
			scroller.setPWidth(pContainer.offsetWidth);
			scroller.setBoundary(0, width * projectMilestoneList.value.length + width);
		});
		obs.observe(pContainer);
	};
	// 计算宽度
	const computedProgressItemWidth = (length: number, parentWidth: number) => {
		const maxWidth = parentWidth / 9;
		if (length > 8) {
			return maxWidth;
		} else if (length == 1) {
			return parentWidth / (length + 1);
		} else {
			return parentWidth / (length + 1);
		}
	};

	onUnmounted(() => {
		obs?.disconnect();
	});

	return { projectMilestoneList, refreshProjectStatus };
};
