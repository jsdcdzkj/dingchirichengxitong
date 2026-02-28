import {
	ElButton,
	ElCol,
	ElForm,
	ElFormItem,
	ElInputNumber,
	ElProgress,
	ElRadioGroup,
	ElRate,
	ElRow,
	ElUpload,
	ElCascader,
	type FormItemRule,
	ElCheckboxGroup
} from "element-plus";
import { computed, defineComponent, h, markRaw, onMounted, reactive, ref, resolveComponent, toRaw, watch } from "vue";
import { array, bool, number, object, string } from "vue-types";
import type { FormItem } from "../page";
import type { Arrayable } from "element-plus/lib/utils/typescript.js";
import Expand from "./expand";
import { ElInput, ElSelect, ElDatePicker, ElCheckbox, ElRadio, ElOption, ElTimePicker } from "element-plus";
import type { SizeType } from "../table/use-table";
import { filterSelectListApi } from "./use-base-form";
import IconSelect from "@/components/icon/IconSelect";
import Editor from "@/components/editor/index.vue";

export default defineComponent({
	components: {
		ElInput,
		ElSelect,
		ElDatePicker,
		ElCheckbox,
		ElRadio,
		ElOption,
		ElTimePicker,
		ElRadioGroup,
		ElProgress,
		ElRate,
		ElUpload,
		IconSelect,
		Editor,
		ElInputNumber,
		ElCascader,
		ElCheckboxGroup,
		ElButton
	},
	props: {
		span: number().def(8),
		minRow: number().def(1),
		formItems: array<FormItem>().def([]),
		rules: object<Record<string, Arrayable<FormItemRule>>>().def(),
		labelPosition: string<any>().def("left"),
		labelWidth: number().def(100),
		inline: bool().def(true),
		value: object().def(),
		showMessage: bool().def(false),
		expand: bool().def(true),
		notMargn: bool().def(false),
		size: string<SizeType>().def(""),
		expandSpan: number().def(8)
	},
	emits: ["update:value", "search", "reset", "instance"],
	setup(props, { emit }) {
		const formInstance = ref();
		const expand = reactive({
			colNum: Math.floor(24 / props.span),
			row: Math.ceil(props.formItems.length / (24 / (props.span + 1))),
			up: false
		});

		props.formItems.forEach((item) => {
			let node = resolveComponent(item.type);
			if (typeof node == "object") {
				item.node = markRaw(node);
			}
		});

		filterSelectListApi(props.formItems);

		const showExpand = computed(() => expand.row > props.minRow);

		const handleSearch = () => {
			emit("search", toRaw(props.value));
		};

		const handleReset = () => {
			formInstance.value?.resetFields();
			emit("reset", toRaw(props.value));
		};

		onMounted(async () => {
			emit("instance", formInstance.value);
		});

		return () => {
			const renderFormItemContent = (item: FormItem) => {
				if (!item.node) return null;
				// 文件上传
				if (item.type == "ElUpload") {
					return h(
						ElUpload,
						{
							fileList: props.value[item.prop],
							style: { width: "100%" },
							...item.attrs,
							"onUpdate:fileList": (e: any) => {
								props.value[item.prop] = e;
							}
						},
						{
							tip: () => {
								return item.attrs?.placeholder;
							},
							default: () => <ElButton>点击上传文件</ElButton>
						}
					);
				}
				// 富文本
				if (item.type == "Editor") {
					return h(Editor, {
						style: { width: "100%" },
						...item.attrs,
						modelValue: props.value[item.prop],
						"onUpdate:modelValue": (e: any) => {
							props.value[item.prop] = e;
						}
					});
				}

				// 富文本
				if (item.type == "ElButton") {
					return h(
						ElButton,
						{
							style: { width: "100%" },
							...item.attrs
						},
						[item.attrs?.button]
					);
				}
				// 默认
				return h(
					item.node,
					{
						style: { width: "100%" },
						...item.attrs,
						modelValue: props.value[item.prop],
						"onUpdate:modelValue": (e: any) => {
							props.value[item.prop] = e;
						}
					},
					{
						default: item.render
					}
				);
			};

			const renderColFormItem = (item: FormItem, index: number) => {
				if (Reflect.has(item, "show") && !item.show) return null;
				if (expand.up && index > expand.colNum - 1 && props.expand) {
					return null;
				}
				const bind = { label: item.label, prop: item.prop, w: "full" };
				return (
					<ElCol span={item.span || props.span} key={index}>
						<ElFormItem {...bind}>{renderFormItemContent(item)}</ElFormItem>
					</ElCol>
				);
			};

			const handleSubmit = (e) => {};

			return (
				<ElForm
					model={props.value}
					rules={props.rules}
					labelPosition={props.labelPosition}
					labelWidth={props.labelWidth}
					inline={props.inline}
					class={["base-form", props.notMargn ? "notMargn" : ""]}
					showMessage={props.showMessage}
					inlineMessage={true}
					ref={formInstance}
					size={props.size}
					onSubmitPrevent={handleSubmit}
				>
					<ElRow gutter={10} w="100%">
						{props.formItems.map((item, index) => renderColFormItem(item, index))}
						{props.expand ? (
							<ElCol span={props.expandSpan}>
								<ElFormItem class="w-100%">
									<Expand
										showExpand={showExpand.value}
										expand={expand.up}
										onReset={handleReset}
										onSearch={handleSearch}
										onExpandChange={() => (expand.up = !expand.up)}
									/>
								</ElFormItem>
							</ElCol>
						) : null}
					</ElRow>
				</ElForm>
			);
		};
	}
});
