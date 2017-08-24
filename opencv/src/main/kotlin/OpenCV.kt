import kotlinx.cinterop.*
import opencv.*

fun main(args: Array<String>) {
    memScoped {
        val cvSize = intArrayOf(24, 24).toCValues().getPointer(this).reinterpret<CvSize>()
        val storage = cvCreateMemStorage(0) ?: run { println("storage is null"); return }
        val cascade = cvLoadHaarClassifierCascade("../../../haarcascade_frontalface_alt.xml", cvSize.pointed.readValue()) ?: run { println("cascade is null"); return }

        val img = cvLoadImage("../../../faces.png", 3) ?: run { println("img is null"); return }
        println("all files are loaded")

        faceDetect(img, storage, cascade)

        println("show image")
        cvNamedWindow("Kotlin/Native Sample", 0)
        cvShowImage("faces", img)
        cvWaitKey(0)
        cvReleaseHaarClassifierCascade(cascade.reinterpret())
        cvReleaseMemStorage(storage.reinterpret())
        cvReleaseImage(img.reinterpret())
    }
}

fun faceDetect(img: CPointer<IplImage>, storage: CPointer<CvMemStorage>, cascade: CPointer<CvHaarClassifierCascade>) {

    memScoped {
        val minSize = intArrayOf(12, 12).toCValues().getPointer(this).reinterpret<CvSize>().pointed
        val maxSize = intArrayOf(256, 256).toCValues().getPointer(this).reinterpret<CvSize>().pointed
        val faces = cvHaarDetectObjects(img, cascade, storage, 1.2, 2, CV_HAAR_DO_CANNY_PRUNING, minSize.readValue(), maxSize.readValue())
        println("faces is null : ${faces == null}")
        for (i in 0 until (faces?.pointed?.total ?: 0)) {
            val faceRect = cvGetSeqElem(faces, i)!!.reinterpret<CvRect>()[0]

            cvRectangle(img, cvPoint(faceRect.x, faceRect.y), cvPoint(faceRect.x + faceRect.width, faceRect.y + faceRect.height), cvScalar(255.0, 0.0, 0.0, 0.0), 3, 1, 0)
        }
    }
}