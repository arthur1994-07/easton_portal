<template>
	<div class="slideshow-wrapper">
		<swiper
			:modules="modules"
			:slides-per-views="1"
			:space-between="0"
			:loop="true"
			:autoplay="{
				delay: 4000,
				disableOnInteraction: false,
			}"
			:pagination="{
				clickable: true,
				dynamicBullets: true,
			}"
			:navigation="true"
			:keyboard="{
				enabled: true,
			}"
			:effect="'slide'"
			:speed="500"
			:grab-cursor="true"
			:touch-ratio="1"
			:threshold="10"
			class="slideshow-swiper"
			@swiper="onSwiper"
			@slideChange="onSlideChange"
		>
			<swiper-slide
				v-for="(item, index) in collections"
				:key="index"
				class="slide"
			>
				<img 
					:src="item.url" 
					:alt="item.alt"
					class="slide-image"
					draggable="false"
				>
				<div class="slide-overlay">
					<h3 class="slide-title">
						{{ currentCollection }}
					</h3>
				</div>
			</swiper-slide>

			<!-- custom play/pause button -->
			<div class="custom-controls">
				<button 
					class="play-pause-button" 
					:aria-label="isPlaying ? 'Pause slideshow' : 'Play slideshow'"
					@click="toggleAutoplay"
				>
					<svg v-if="isPlaying" viewBox="0 0 24 24" fill="currentColor">
						<rect x="6" y="4" width="4" height="16" />
						<rect x="14" y="4" width="4" height="16" />
					</svg>
					<svg v-else viewBox="0 0 24 24" fill="currentColor">
						<polygon points="5,3 19,12 5,21" />
					</svg>
				</button>

				<!-- Slide counter -->
				<div class="slide-counter">
					{{ currentSlide + 1 }} / {{ collections.length }}
				</div>
			</div>
		</swiper>
	</div>
</template>

<script>
'use strict'

import { defineComponent, ref, toRefs, onMounted } from "vue";
import { Swiper, SwiperSlide } from 'swiper/vue'
import { Navigation, Pagination, Autoplay, Keyboard, EffectFade } from 'swiper/modules'
import 'swiper/css'
import 'swiper/css/navigation'
import 'swiper/css/pagination'
import 'swiper/css/autoplay'
import 'swiper/css/keyboard'
import 'swiper/css/effect-fade'

export default defineComponent({
	components: {
		Swiper,
		SwiperSlide
	},
	props: {
		images: {required: true }
	},
	setup(props) {
		const { images } = toRefs(props)
		const currentSlide = ref(0)
		const isPlaying = ref(true)
		const swiperInstance = ref(null)
		const currentCollection = ref(null)
		const collections = ref([{
			url: null,
			alt: null
		}])

		const modules = [ Navigation, Pagination, Autoplay, Keyboard, EffectFade ]
		// Swiper event handler
		const onSwiper = (swiper) => {
			swiperInstance.value = swiper;
		}

		const onSlideChange = (swiper) => {
			currentSlide.value = swiper.realIndex
		}


		// Control functions
		const toggleAutoplay = () => {
			if (!swiperInstance.value) return
			
			if (isPlaying.value) {
				swiperInstance.value.autoplay.stop()
			} else {
				swiperInstance.value.autoplay.start()
			}
		}

		onMounted(() => {
			collections.value = images.value.images.map(s => ({
				url: ref(new URL("data:image/png;base64, "+ s.image, import.meta.url).href),
			}))
		})

		return { collections, currentSlide, isPlaying, currentCollection, modules, onSwiper, onSlideChange, 
			toggleAutoplay }
	}
})
</script>

<style>
.slideshow-container {
	/* min-height: 60vh; */
	width: 100%;
	height: 100%;
	background: linear-gradient(135deg, #ffffff 0%, #abd7bb 100%);
	display: flex;
	justify-content: center;
	padding: 10rem;
	font-family: Avenir, Helvetica, Arial, sans-serif;
}

.slideshow-wrapper {
	width: auto;
	max-width: 1200px;
	height: auto;
	position: relative;
}

.slideshow-swiper {
	width: 100%;
	height: 70vh;
	border-radius: 20px;
	overflow: hidden;
	box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.4);
	background: #000;
}

.slide {
	min-width: 100%;
	height: 100%;
	position: relative;
	display: flex;
	align-items: center;
	justify-content: center;
}

.slide-image {
	max-width: 100%;
	max-height: 100%;
	width: auto;
	height: auto;
	object-fit: contain;
	object-position: center;
	transition: transform 0.3s ease;
	display: block;
}

.slide-overlay {
	position: absolute;
	bottom: 0;
	left: 0;
	right: 0;
	background: linear-gradient(transparent, rgba(0, 0, 0, 0.8));
	color: white;
	padding: 3rem 2rem 2rem;
	transform: translateY(100%);
	transition: transform 0.3s ease;
}

.swiper-slide-active .slide-overlay {
	transform: translateY(0);
}

.swiper-slide-active .slide-image {
	transform: scale(1.1);
}

.slide-title {
	font-size: 1.8rem;
	font-weight: 700;
	margin: 0 0 0.5rem 0;
	line-height: 1.2;
	text-align: left;
}

.slide-description {
	font-size: 1rem;
	margin: 0;
	opacity: 0.9;
	line-height: 1.5;
	text-align: left;
}

.custom-controls {
	position: absolute;
	top: 0;
	left: 0;
	right: 0;
	bottom: 0;
	pointer-events: none;
	z-index: 10;
}

.slide-counter {
	position: absolute;
	bottom: 2rem;
	right: 2rem;
	background: rgba(0, 0, 0, 0.6);
	color: white;
	padding: 0.5rem 1rem;
	border-radius: 20px;
	font-size: 0.9rem;
	font-weight: 600;
	backdrop-filter: blur(10px);
	pointer-events: auto;
}


.play-pause-button {
	position: absolute;
	top: 2rem;
	right: 2rem;
	background: rgba(0, 0, 0, 0.6);
	border: none;
	width: 50px;
	height: 50px;
	border-radius: 50%;
	color: white;
	cursor: pointer;
	transition: all 0.3s ease;
	backdrop-filter: blur(10px);
	display: flex;
	align-items: center;
	justify-content: center;
	pointer-events: auto;
}

.play-pause-button:hover {
	background: rgba(0, 0, 0, 0.8);
	transform: scale(1.1);
}

.play-pause-button svg {
	width: 20px;
	height: 20px;
}

.return-button {
	background: #1C4854;
}

/* Swiper custom styling */
.swiper-button-next,
.swiper-button-prev {
	background: rgba(255, 255, 255, 0.2);
	width: 60px;
	height: 60px;
	border-radius: 50%;
	backdrop-filter: blur(10px);
	transition: all 0.3s ease;
}

.swiper-button-next:hover,
.swiper-button-prev:hover {
	background: rgba(255, 255, 255, 0.3);
	transform: scale(1.1);
}

.swiper-button-next::after,
.swiper-button-prev::after {
	font-size: 20px;
	font-weight: bold;
	color: white;
}

.swiper-pagination {
	bottom: 2rem !important;
	left: 50% !important;
	transform: translateX(-50%) !important;
	width: auto !important;
	display: flex;
	justify-content: center;
	gap: 1rem;
}

.swiper-pagination-bullet {
	width: 12px;
	height: 12px;
	background: rgba(255, 255, 255, 0.4);
	opacity: 1;
	transition: all 0.3s ease;
}

.swiper-pagination-bullet:hover {
	background: rgba(255, 255, 255, 0.6);
	transform: scale(1.2);
}

.swiper-pagination-bullet-active {
	background: #3B82F6;
	transform: scale(1.3);
}

/* Mobile responsiveness */
@media (max-width: 768px) {
	.slideshow-container {
		padding: 1rem;
	}
	
	.slideshow-swiper {
		height: 80vh;
		min-height: 400px;
		border-radius: 15px;
	}
	
	.swiper-button-next,
	.swiper-button-prev {
		width: 50px;
		height: 50px;
	}
	
	:deep(.swiper-button-next::after),
	:deep(.swiper-button-prev::after) {
		font-size: 16px;
	}
	
	.play-pause-button {
		top: 1rem;
		right: 1rem;
		width: 40px;
		height: 40px;
	}
	
	.slide-overlay {
		padding: 2rem 1.5rem 1.5rem;
	}
	
	.slide-title {
		font-size: 1.4rem;
	}
	
	.slide-description {
		font-size: 0.9rem;
	}
	
	.slide-counter {
		bottom: 1rem;
		right: 1rem;
		font-size: 0.8rem;
	}
	
	.swiper-pagination {
		bottom: 1.5rem !important;
	}
}

@media (max-width: 480px) {
	.slideshow-swiper {
		height: 50vh;
		min-height: 350px;
	}
	
	.swiper-button-next,
	.swiper-button-prev {
		width: 45px;
		height: 45px;
	}
	
	.swiper-button-next::after,
	.swiper-button-prev::after {
		font-size: 14px;
	}
}
</style>