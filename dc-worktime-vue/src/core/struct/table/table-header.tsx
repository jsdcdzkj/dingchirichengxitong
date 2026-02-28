import { Refresh, Filter, Setting } from "@element-plus/icons-vue";
import {
	ElCheckbox,
	ElCheckboxGroup,
	ElDivider,
	ElDropdown,
	ElDropdownItem,
	ElDropdownMenu,
	ElIcon,
	ElPopover
} from "element-plus";
import { defineComponent, ref } from "vue";
import { array, string } from "vue-types";
import type { TableColumnType } from "./use-table";
export default defineComponent({
	props: {
		title: string().def("标题"),
		columns: array<TableColumnType>().def([]),
		value: array<string>().def([])
	},
	emits: ["command", "update:value"],
	setup(props, { slots, emit }) {
		const columnKey = ref([...props.value]);
		return () => (
			<div
				class="flex  p-b-10px flex-items-center flex-justify-between m-b-12px w-full"
				style="position:relative"
			>
				<div class="font-size-18px font-b font-bold w-full" style="text-align:left">
					{slots.title ? slots.title() : (props.title ?? null)}
				</div>
				{slots.default ? <div class="flex  flex-items-center gap-10px">{slots.default()}</div> : null}

				<ElDivider
					style="position:absolute;left:-12px;bottom:0;width:calc(100% + 24px)"
					class="m-t-0px m-b-0px"
				></ElDivider>
			</div>
		);
	}
});
