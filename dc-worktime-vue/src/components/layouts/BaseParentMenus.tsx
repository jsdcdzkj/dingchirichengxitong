import {
	ElMenu,
	ElMenuItem,
	ElMenuItemGroup,
	ElPopover,
	ElSubMenu
} from "element-plus";
import { defineComponent, h } from "vue";
import { array, bool, string } from "vue-types";

export default defineComponent({
	props: {
		collapse: bool().def(false),
		rawList: array().def([]),
		defaultActive: string().def(),
		chList: array().def([]),
		chActive: string().def(),
		defaultOpend: array<string>().def()
	},
	setup(props) {
		const renderItem = (item: any) => {
			return h(ElMenuItem, { index: item.path, key: item.path }, () => [
				h("img", {
					class: "menu-icon size-26px ",
					src: `/module/icon-${props.defaultActive == item.path ? item.meta.activeIcon : item.meta.icon}.png`
				}),
				item.meta.title
			]);
		};

		function getCMenuChStruct(menusList: any[]) {
			return menusList.map((item: any) => {
				if (item.children && item.children.length > 0) {
					const menuItems = getCMenuChStruct(item.children);
					return h(
						ElSubMenu,
						{ index: item.path },
						{
							title: () => h("span", item.name),
							default: () => menuItems
						}
					);
				} else {
					return h(
						ElMenuItem,
						{ index: item.path },
						() => item.title
					);
				}
			});
		}

		function renderGroup(name: string, nodes: any[]) {
			return (
				<ElMenuItemGroup>
					{{
						title: () => name,
						default: () => nodes
					}}
				</ElMenuItemGroup>
			);
		}

		function getCMenuStruct(menusList: any[]) {
			return menusList.map((item: any) => {
				const pRouterRaw = props.chList.find(
					(it) => it.path == item.path
				);

				return h(
					ElPopover,
					{
						showArrow: false,
						placement: "right-start",
						offset: 20,
						width: 192,
						key: item.path,
						disabled: !props.collapse
					},
					{
						default: () => (
							<ElMenu
								router
								defaultActive={props.chActive}
								defaultOpeneds={props.defaultOpend}
								class="ep-menu-not-border popover-custom transition-none w-100%"
								mode="vertical"
								uniqueOpened={true}
							>
								{renderGroup(
									item.meta.title,
									getCMenuChStruct(pRouterRaw.children)
								)}
							</ElMenu>
						),
						reference: () => renderItem(item)
					}
				);
			});
		}

		return () => {
			return (
				<ElMenu
					router
					defaultActive={props.defaultActive}
					class="ep-menu-not-border custom-menu transition-none"
					mode="vertical"
					collapse={props.collapse}
				>
					{getCMenuStruct(props.rawList)}
				</ElMenu>
			);
		};
	}
});
