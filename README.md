## Info
> 매일매일 DS 두뇌 트레이닝 계산 n회 오마주 하기 <br/>
> 개발 기간: 24.05.14 ~ 28 (2주)
<br/>

> 참고 영상 <br/>

[![Video Label](http://img.youtube.com/vi/QCrpuBLKIUY/0.jpg)](https://youtu.be/QCrpuBLKIUY&t=71s)

## Stack
- MVVM
- Jetpack Compose
- DataStore(Preferences)
- Hilt


## Trouble Shooting
<details>
<summary>mlkit 라이브러리를 사용해야 할까?</summary>
<div markdown="1">

> mlkit digitInk-recognition은 손으로 필기한 내용을 인식해주는 라이브러리이다. <br/>
> Ink.Builder()와 .addStroke를 사용하여 필기를 기억하고 제공해주는 모델을 설치하여 .recognize(ink)를 수행한다. <br/>
  
> 1Q. <br/> 예제 코드는 내용을 필기하기 위해 MotionEvent(DOWN, MOVE, UP)를 이용하는데 이는 사용자가 손을 떼어야 필기가 진행된다. <br/>
> 2Q. <br/> 숫자 인식기가 없다. <br/>

> 1A. <br/> Modifier.pointerInput의 scope 안 detectDragGestures에서 유저의 입력을 저장했다. <br/>
> (ps.compose에서 MotionEvent는 pointerInteropFilter로 처리) <br/>
> 저장한 유저의 입력을 Canvas에 그리고 Canvas를 모델의 input인 Bitmap으로 만들어주기 위해 <br/>
> compose 1.7.0-beta02에 추가된 graphicsLayer를 사용했다. <br/>

```kotlin
val drawModifier = modifier
        .pointerInput(Unit) {
            if (checkDrawResult == null) {
                detectDragGestures(
                    onDragEnd = {
                        coroutineScope.launch {
                            async {
                                imageBitmap = graphicsLayer.toImageBitmap()
                            }.await()
                        }
                    }
                ) { change, dragAmount ->
//                Log.d("change", "$change, $dragAmount")
                    val path =
                        PathState(
                            start = change.position - dragAmount,
                            end = change.position
                        )
                    userPaths.add(path)
                }
            }
        }
        .drawWithContent {
            graphicsLayer.record {
                // draw the contents of the composable into the graphics layer
                this@drawWithContent.drawContent()
            }
            // draw the graphics layer on the visible canvas
            drawLayer(graphicsLayer)
        }
```
> 2A. <br/> mnist dataSet을 이용한 숫자 인식 모델을 만드는 자료는 많았기 때문에 직접 모델을 골랐다.

</div>
</details>

<details>
<summary>숫자 인식 모델 처리</summary>
<div markdown="1">

> Kaggle에서 추천수, 정확도 높은 [CNN 모델](https://www.kaggle.com/code/aruneshhh/cnn-model-accuracy-98-mnist)을 tfLite로 변환해서 앱에 내장시켰다. <br/>
> TensorImage에서 compose ImageBitmap을 사용하지 않았기 때문에 AndroidBitmap과 Config.ARGB_8888로 변환, <br/>
> input 값에 맞추어 imageProcessor에서 리사이징과 회색조 처리(추가 라이브러리 필요)를 적용하였다. <br/>

> 정상적으로 빌드되었으나 다른 입력에도 계속 같은 confidences를 반환하였다. <br/>
> 유저 입력을 가시적으로 초록 배경에 까만 글씨로 두었는데 어두운 배경에 밝은 글씨로 모델 학습의 이미지와 같은 환경을 만들어주었다. <br/>
> 회색조 처리가 정반대로 되고 있었던 것 같다. 이후 다크모드 테마를 기본으로 설정하였다. <br/>

</div>
</details>

<details>
<summary>스크롤 처리</summary>
<div markdown="1">

> 입력 없으면 5.5초 후에 자동 스크롤과 입력 후에 0.5초 후 스크롤을 함께 수행해야 했다. <br/>
> LaunchedEffect에 각 스크롤을 관리하는 key 2개를 제공하고 <br/>
> 5.5초와 0.5초 각각을 coroutine으로 delay 처리하였다. <br/>
> 유저 입력을 받을 시 자동 5.5 스크롤의 job을 취소하고 <br/>
> try~finally와 if문으로 스크롤이 여러번 수행되지 않게 관리하였다. <br/>

```kotlin
LaunchedEffect(
        key1 = autoScroll,
        key2 = isDraw
    ) {
        val nextIdx = listState.firstVisibleItemIndex + 1
        if (!isDraw) {
            delayJob = launch {
                try { // cancelException 예외 처리
                    delay(5.seconds)
                    onCheckDrawResult(null, nextIdx - 1) // checkDrawResult == false
                    delay(0.5.seconds)
                    listState.animateScrollToItem(nextIdx)
                } finally {
                    if (nextIdx == qnaList.size) onTerminate()
                    else if (!isDraw) autoScroll = !autoScroll
                }
            }
        } else {
            delayJob?.cancelAndJoin()
            delay(0.5.seconds)
            if (nextIdx < qnaList.size) {
                listState.animateScrollToItem(nextIdx)
                isDraw = false
            }
        }
    }
```

</div>
</details>

## 아쉬운 점
> 모델의 성능이 아쉽지만 tfLite기도 하고, 성능과 속도는 trede-off기 때문에.. 크기가 5.5mb여서 내장해보았다. <br/>
> mnist가 단일 숫자 데이터라 계산식의 결과가 단일 값만 나오게 처리되어 있다.
> 숫자 4처럼 유저 드래그를 2번 이상 받아야하는 경우에 delay 시간과 스크롤 처리가 복잡해져서 일단은 한붓그리기가 되었다. <br/>




## 구현 영상
<video src="https://github.com/jbrunoo/digitInk/assets/125545555/997dce7f-0740-409f-b6de-0e764043f085" width="30%">
