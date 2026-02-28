import { h, reactive, resolveComponent } from "vue";
import type { FormItem } from "../page";
import type { Arrayable } from "element-plus/lib/utils/typescript.js";
import type { FormInstance, FormItemRule } from "element-plus";
import { useDialogForm } from "@/core/dialog/dialog-container";
import type { SizeType } from "../table/use-table";

function bindForm(formItems: FormItem[]) {
	return formItems.reduce(
		(form, item) => {
			if (item.prop) {
				form[item.prop] = item.value;
			}
			return form;
		},
		{} as Record<string, any>
	);
}

function renderSelectOptin(item: FormItem) {
	if (item.select && !item.render) {
		const isRadio = item.type == "ElRadioGroup";
		const label = isRadio ? item.select.value : item.select.label;
		const value = isRadio ? item.select.label : item.select.value;
		return () =>
			item.select?.list?.map((selectItem) => {
				let l = label ? selectItem[label] : selectItem["label"];
				let v = value ? selectItem[value] : selectItem["value"];

				return h(
					resolveComponent(item.select!!.type),
					{
						label: l,
						value: v,
						key: v,
						disabled: selectItem["disabled"]
					},
					() => l
				);
			});
	}
	return undefined;
}

export async function filterSelectListApi(formItems: FormItem[]) {
	for (let i = 0; i < formItems.length; i++) {
		const item = formItems[i];

		switch (item.type) {
			case "ElSelect":
			case "ElCheckboxGroup":
			case "ElRadioGroup":
				if (item.select) {
					if (item.select.listApi) {
						item.select.list = await item.select.listApi(item);
					}
					if (!item.render) {
						item.render = renderSelectOptin(item);
					}
				}
				break;

			case "ElCascader":
				if (item.select && item.select.listApi && item.attrs) {
					item.attrs.options = await item.select.listApi(item);
				}
				break;
		}
	}

	return formItems;
}

export function filterSelectRender(formItems: FormItem[]) {
	for (let i = 0; i < formItems.length; i++) {
		const item = formItems[i];
		switch (item.type) {
			case "ElSelect":
			case "ElRadioGroup":
			case "ElCheckboxGroup":
				if (item.select) {
					if (!item.render) {
						item.render = renderSelectOptin(item);
					}
				}
				break;
			// case "ElCascader":
			// 	if (item.attrs) {
			// 		if (!item.render) {
			// 			item.render = renderSelectOptin(item);
			// 		}
			// 	}
			// 	break;
			default:
				break;
		}
	}

	return formItems;
}

export type BaseFormConfig = {
	formItems: FormItem[];
	rules?: Record<string, Arrayable<FormItemRule>>;
	labelPosition?: "left" | "right" | "top";
	inline?: boolean;
	labelWidth?: number;
	inlineMessage?: boolean;
	scrollToError?: boolean;
	gutter?: number;
	span?: number;
	showMessage?: boolean;
	expand?: boolean;
	notMargn?: boolean;
	size?: SizeType;
	expandSpan?: number;
	value?: any;
};

export type FormConfig = {
	onReset?: (data?: any) => void;
	onSearch?: (data?: any) => void;
	onInstance?: (instance: FormInstance) => void;
	className?: string;
} & BaseFormConfig;

export const useForm = (config: FormConfig) => {
	const formStruct = reactive({
		...config,
		value: bindForm(config.formItems),
		formItems: config.formItems
	});

	const Api = {
		setFormItems(formItems: FormItem[]) {
			formStruct.formItems = filterSelectRender(formItems);
		}
	};
	return { form: formStruct, Api };
};

export const useAsyncForm = async (config: FormConfig) => {
	await filterSelectListApi(config.formItems);
	return useForm(config);
};

export const useDialogStructForm = (config: FormConfig, isBindInstance = true) => {
	const { registerFormDone, registerFormInstrance, formInstance } = useDialogForm();
	const { form, Api } = useForm(config);
	if (form && isBindInstance) {
		form!!.onInstance = registerFormInstrance;
	}
	return { form, Api, registerFormDone };
};
