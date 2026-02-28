<template>
	<div class="el-quarter-picker">
		<ElPopover
			v-model:visible="visible"
			:disabled="!canPopover"
			:tabindex="null"
			placement="bottom-start"
			transition="el-zoom-in-top"
			trigger="click"
			:width="346"
		>
			<template #reference>
				<ElInput
					@change="changeText"
					@mouseenter="mouseEnter"
					@mouseleave="mouseLeave"
					:placeholder="placeholder"
					:prefix-icon="Calendar"
					v-model="text"
					:size="size"
					:readonly="!canEdit"
					:disabled="disabled"
				>
					<!-- <i slot="prefix" class="el-input__icon el-icon-date"></i> -->
					<template #suffix>
						<el-icon class="el-input__icon" v-show="showClear" style="cursor: pointer" @click.stop="clear"
							><Close
						/></el-icon>
					</template>
				</ElInput>
			</template>
			<div class="el-date-picker">
				<div class="el-picker-panel__body">
					<div
						class="el-date-picker__header el-date-picker__header--bordered"
						style="margin: 0px; line-height: 30px"
					>
						<button
							type="button"
							@click="clickLast"
							aria-label="前一年"
							class="el-picker-panel__icon-btn el-date-picker__prev-btn el-icon-d-arrow-left"
						>
							<el-icon class="el-input__icon"><DArrowLeft /></el-icon>
						</button>
						<span role="button" class="el-date-picker__header-label" @click="clickYear">{{ title }}</span>
						<button
							type="button"
							@click="clickNext"
							aria-label="后一年"
							class="el-picker-panel__icon-btn el-date-picker__next-btn el-icon-d-arrow-right"
						>
							<el-icon class="el-input__icon"><DArrowRight /></el-icon>
						</button>
					</div>
					<div class="el-picker-panel__content" style="margin: 0px; width: 100%">
						<table class="el-month-table" style="">
							<tbody>
								<tr v-for="line in lineCount" :key="line">
									<td
										v-for="index in line * 4 <= viewList.length
											? 4
											: viewList.length - (line - 1) * 4"
										:key="index"
										:class="{
											today: viewList[(line - 1) * 4 + index - 1].current,
											current: viewList[(line - 1) * 4 + index - 1].active
										}"
									>
										<div>
											<a
												class="cell"
												:class="{
													textDis: isOverTime(
														viewList[(line - 1) * 4 + index - 1].year,
														viewList[(line - 1) * 4 + index - 1].quarter
													)
												}"
												@click="clickItem(viewList[(line - 1) * 4 + index - 1])"
												>{{ viewList[(line - 1) * 4 + index - 1].label }}</a
											>
										</div>
									</td>
								</tr>
							</tbody>
						</table>
					</div>
				</div>
			</div>
		</ElPopover>
	</div>
</template>

<script setup lang="tsx">
	import { ref, computed, watch, onMounted, onBeforeUnmount } from "vue";
	import { ElPopover, ElInput, ElButton } from "element-plus";
	import { Search, Calendar, Close, DArrowLeft, DArrowRight } from "@element-plus/icons-vue";

	const props = defineProps({
		placeholder: {
			type: String,
			default: ""
		},
		size: {
			type: String,
			default: ""
		},
		readonly: {
			type: Boolean,
			default: false
		},
		clearable: {
			type: Boolean,
			default: true
		},
		editable: {
			type: Boolean,
			default: true
		},
		disabled: {
			type: Boolean,
			default: false
		},
		format: {
			type: String,
			default: "yyyy年第Q季度"
		},
		valueFormat: {
			type: String,
			default: "yyyy-qq"
		},
		modelValue: {
			type: String,
			default: ""
		},
		minYear: {
			type: Number,
			default: 1000
		},
		maxYear: {
			type: Number,
			default: 9999
		}
	});

	const emit = defineEmits(["update:modelValue", "change"]);

	// Reactive state
	const visible = ref(false);
	const showClear = ref(false);
	const canEdit = ref(true);
	const canPopover = ref(true);
	const text = ref("");
	const viewType = ref(1); // 1: quarter, 2: year
	const viewYear = ref(0);
	const data = ref([0, 0]); // [year, quarter]

	// Computed properties
	const title = computed(() => {
		if (viewType.value === 1) {
			return `${viewYear.value} 年`;
		} else {
			const start = Math.floor(viewYear.value / 10) * 10;
			return `${start} 年 - ${start + 9} 年`;
		}
	});

	const viewList = computed(() => {
		const list = [];
		const curDate = new Date();
		const curYear = curDate.getFullYear();
		const curQuarter = Math.floor(curDate.getMonth() / 3) + 1;

		if (viewType.value === 1) {
			let index = 0;
			for (const i of "1234") {
				index++;
				const item = {
					label: `Q${i}`,
					year: viewYear.value,
					quarter: index,
					current: false,
					active: false
				};

				if (viewYear.value === curYear && index === curQuarter) {
					item.current = true;
				} else if (viewYear.value === data.value[0] && index === data.value[1]) {
					item.active = true;
				}
				list.push(item);
			}
		} else {
			const start = Math.floor(viewYear.value / 10) * 10;
			viewYear.value = start;
			for (let i = 0; i < 10; i++) {
				const year = start + i;
				const item = {
					label: `${year}`,
					year: year,
					current: false,
					active: false
				};

				if (year === curYear) {
					item.current = true;
				} else if (year === data.value[0]) {
					item.active = true;
				}
				list.push(item);
			}
		}
		return list;
	});

	const lineCount = computed(() => {
		const count = Math.floor(viewList.value.length / 4);
		return viewList.value.length % 4 > 0 ? count + 1 : count;
	});

	// Methods
	const changeText = () => {
		if (checkFormat(props.format, text.value)) {
			console.log("text.value", text.value);
			formatFrom(text.value, props.format);
			// if (!isOverTime(item.year, item.quarter)) return
			emit("update:modelValue", formatTo(data.value, props.valueFormat));
			emit("change", formatTo(data.value, props.valueFormat));
		} else {
			if (data.value[0] < 1 || data.value[1] < 1) {
				text.value = "";
			} else {
				text.value = formatTo(data.value, props.format);
			}
		}
		visible.value = false;
	};

	const mouseEnter = () => {
		if (!props.disabled && !props.readonly && props.clearable && text.value !== "") {
			showClear.value = true;
		}
	};

	const mouseLeave = () => {
		if (!props.disabled && props.clearable) {
			showClear.value = false;
		}
	};

	const clear = () => {
		showClear.value = false;
		visible.value = false;
		emit("update:modelValue", "");
		emit("change", "");
	};

	const changeValue = (val) => {
		viewType.value = 1;
		if (val) {
			formatFrom(val, props.valueFormat);
			text.value = formatTo(data.value, props.format);
			viewYear.value = data.value[0];
		} else {
			text.value = "";
			data.value = [0, 0];
			viewYear.value = new Date().getFullYear();
		}
	};

	const checkFormat = (pattern, val) => {
		let text = "";
		for (const char of pattern) {
			const dict = "\\^$.+?*[]{}!";
			text += dict.includes(char) ? `\\${char}` : char;
		}
		text = text.replace("yyyy", "[1-9]\\d{3}").replace("qq", "0[1-4]").replace("q", "[1-4]").replace("Q", "[1234]");
		return new RegExp(`^${text}$`).test(val);
	};

	const formatTo = (data, pattern) => {
		return pattern
			.replace("yyyy", `${data[0]}`)
			.replace("qq", `0${data[1]}`)
			.replace("q", `${data[1]}`)
			.replace("Q", "1234".substr(data[1] - 1, 1));
	};

	const formatFrom = (str, pattern) => {
		const year = findText(str, pattern, "yyyy");
		const quarter = findText(str, pattern, ["qq", "q", "Q"]);
		data.value = [year, quarter];
	};

	const findText = (str, pattern, find) => {
		if (Array.isArray(find)) {
			for (const f of find) {
				const val = findText(str, pattern, f);
				if (val !== -1) return val;
			}
			return -1;
		}

		const index = pattern.indexOf(find);
		if (index === -1) return index;

		const val = str.substr(index, find.length);
		if (find === "Q") return "1234".indexOf(val) + 1;
		return parseInt(val);
	};

	const clickYear = () => {
		if (viewType.value !== 1) return;
		viewType.value = 2;
	};

	const getQuarter = () => {
		var now = new Date();
		var month = now.getMonth() + 1;
		var quarter;

		if (month >= 1 && month <= 3) {
			quarter = 1;
		} else if (month >= 4 && month <= 6) {
			quarter = 2;
		} else if (month >= 7 && month <= 9) {
			quarter = 3;
		} else {
			quarter = 4;
		}
		return quarter;
	};

	const isOverTime = (year, quarter) => {
		const yearCurrent = new Date().getFullYear();
		const quarterCurrent = getQuarter();
		return year - yearCurrent > 0 || (year == yearCurrent && quarter > quarterCurrent);
	};

	const clickItem = (item) => {
		if (viewType.value === 1) {
			console.log("999999999999item", item);
			if (isOverTime(item.year, item.quarter)) return;
			emit("update:modelValue", formatTo([item.year, item.quarter], props.valueFormat));
			emit("change", formatTo([item.year, item.quarter], props.valueFormat));
			visible.value = false;
		} else {
			viewType.value = 1;
			viewYear.value = item.year;
		}
	};

	const clickLast = () => {
		if (viewYear.value > props.minYear) {
			viewYear.value -= viewType.value === 1 ? 1 : 10;
		}
	};

	const clickNext = () => {
		if (viewYear.value < props.maxYear) {
			viewYear.value += viewType.value === 1 ? 1 : 10;
		}
	};

	const handleKeyDown = (event) => {
		if (visible.value) {
			const [year, quarter] = data.value;
			let newYear = year;
			let newQuarter = quarter;

			if (year < 1 || quarter < 1) {
				const curDate = new Date();
				newYear = curDate.getFullYear();
				newQuarter = Math.floor(curDate.getMonth() / 3) + 1;
			}

			switch (event.code) {
				case "ArrowLeft":
					newQuarter = quarter === 1 ? 4 : quarter - 1;
					newYear = quarter === 1 ? year - 1 : year;
					break;
				case "ArrowRight":
					newQuarter = quarter === 4 ? 1 : quarter + 1;
					newYear = quarter === 4 ? year + 1 : year;
					break;
				case "ArrowUp":
					newYear = year - 1;
					break;
				case "ArrowDown":
					newYear = year + 1;
					break;
				default:
					return;
			}

			if (newYear >= props.minYear && newYear <= props.maxYear) {
				data.value = [newYear, newQuarter];
				viewType.value = 1;
				viewYear.value = newYear;
				if (isOverTime(year, quarter)) return;
				emit("update:modelValue", formatTo(data.value, props.valueFormat));
				emit("change", formatTo(data.value, props.valueFormat));
			}
		}
	};

	// Watchers
	watch(
		() => props.modelValue,
		(val) => {
			changeValue(val);
		}
	);

	watch(
		() => props.readonly,
		(val) => {
			canEdit.value = !val && props.editable;
			canPopover.value = !props.disabled && !val;
		}
	);

	watch(
		() => props.editable,
		(val) => {
			canEdit.value = !props.readonly && val;
		}
	);

	watch(
		() => props.disabled,
		(val) => {
			canPopover.value = !val && !props.readonly;
		}
	);

	// Lifecycle hooks
	onMounted(() => {
		changeValue(props.modelValue);
		canEdit.value = !props.readonly && props.editable;
		canPopover.value = !props.disabled && !props.readonly;
		document.addEventListener("keydown", handleKeyDown);
	});

	onBeforeUnmount(() => {
		document.removeEventListener("keydown", handleKeyDown);
	});
</script>

<style lang="scss">
	.el-quarter-picker {
		width: 260px;
		display: inline-block;
	}
	.cell {
		color: rgb(96, 98, 102);
	}
	.textDis {
		color: rgba(0, 0, 0, 0.25);
	}
	.current,
	.today {
		.cell {
			color: #409eff;
		}
	}
</style>
