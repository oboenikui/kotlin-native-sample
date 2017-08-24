import kotlinx.cinterop.*
import opencv.*

fun main(args: Array<String>) {
    memScoped {
        val storage = cvCreateMemStorage(0) ?: run { println("storage is null"); return }
        val cascade: CPointer<CvHaarClassifierCascade> =
                cvLoadHaarClassifierCascade("haarcascade_frontalface_alt.xml", cvSize(24, 24))
                        ?: run { println("cascade is null"); return }

        val img = cvLoadImage("faces.png", 3) ?: run { println("img is null"); return }
        faceDetect(img, storage, cascade)

        println("face detect completed")

        cvNamedWindow("Kotlin/Native Sample", 0)
        cvShowImage("faces", img)
        cvWaitKey(0)

        println("release phase")
        cvReleaseHaarClassifierCascade(allocPointerTo<CvHaarClassifierCascade>().apply { value = cascade }.ptr)
        cvReleaseMemStorage(allocPointerTo<CvMemStorage>().apply { value = storage }.ptr)
        cvReleaseImage(allocPointerTo<IplImage>().apply { value = img }.ptr)
    }
}

fun faceDetect(img: CPointer<IplImage>, storage: CPointer<CvMemStorage>, cascade: CPointer<CvHaarClassifierCascade>) {

    memScoped {
        val faces = cvHaarDetectObjects(img, cascade, storage, 1.2, 2, CV_HAAR_DO_CANNY_PRUNING, cvSize(12, 12), cvSize(256, 256))
        for (i in 0 until (faces?.pointed?.total ?: 0)) {
            val faceRect = cvGetSeqElem(faces, i)!!.reinterpret<CvRect>()[0]
            cvRectangle(img, cvPoint(faceRect.x, faceRect.y), cvPoint(faceRect.x + faceRect.width, faceRect.y + faceRect.height), cvScalar(255.0, 0.0, 0.0, 0.0), 3, 1, 0)
        }
    }
}