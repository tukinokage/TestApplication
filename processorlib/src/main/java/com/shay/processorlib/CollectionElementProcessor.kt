
import com.google.auto.service.AutoService
import java.io.File
import java.util.logging.Logger
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.Processor
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.element.TypeElement

/**
 * PACK com.masadoraandroid.site.annotation
 * CREATE BY Shay
 * DATE BY 2022/12/1 11:25 星期四
 * <p>
 * DESCRIBE
 * <p>
 */
// TODO:2022/12/1 
@AutoService(Processor::class)
class CollectionElementProcessor: AbstractProcessor() {
    override fun process(
        annotations: MutableSet<out TypeElement>?,
        roundEnv: RoundEnvironment?
    ): Boolean {
        if (roundEnv != null) {
            var elements =
                roundEnv.getElementsAnnotatedWith(CollectionElement::class.java)
            elements.forEach {
                val typeElement = it as TypeElement
                val file =
            }
        }
    }
}