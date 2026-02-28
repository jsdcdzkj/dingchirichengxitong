import { defineComponent, h } from "vue";
import { array, number, string } from "vue-types";
import ProgressItem from "./progress-item.vue";
import { ElEmpty } from "element-plus";

export default defineComponent({
	props: {
		progressList: array().def(),
		label: string().def("label"),
		time: string().def("time"),
		progress: number().def()
	},

	setup(props) {
		return () => {
			const children = props.progressList.map((item, index) => {
				return <ProgressItem item={item} label={props.label} time={props.time} key={index} />;
			});

			return (
				<div
					class="flex progress-list-scroller"
					style={{
						position: "relative",
						width: "100%",
						padding: "10px 0"
					}}
				>
					{children.length > 0 ? [...children] : <ElEmpty w="100%" />}
				</div>
			);
		};
	}
});
