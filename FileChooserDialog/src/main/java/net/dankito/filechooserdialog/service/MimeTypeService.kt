package net.dankito.filechooserdialog.service

import android.webkit.MimeTypeMap
import java.io.File


class MimeTypeService {

    fun getMimeType(file: File): String? {
        val extension = file.extension
        if(extension.isNotBlank()) {
            return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension.toLowerCase())
        }

        return null
    }


    fun isImageFile(mimeType: String): Boolean {
        return mimeType.startsWith("image/", true)
    }

    fun isAudioFile(mimeType: String): Boolean {
        return mimeType.startsWith("audio/", true)
    }

    fun isVideoFile(mimeType: String): Boolean {
        return mimeType.startsWith("video/", true)
    }

    fun isTextFile(mimeType: String): Boolean {
        return mimeType.startsWith("text/", true)
    }


    fun isDocument(mimeType: String): Boolean {
        return isTextFile(mimeType) || isPdfFile(mimeType) || isMicrosoftOfficeFile(mimeType) || isOpenOfficeFile(mimeType)
    }

    fun isPdfFile(mimeType: String): Boolean {
        return "application/pdf".equals(mimeType, true)
    }


    fun isMarkUpFile(mimeType: String): Boolean {
        return isHtmlFile(mimeType) || isXmlFile(mimeType)
    }

    fun isHtmlFile(mimeType: String): Boolean {
        return "text/html".equals(mimeType, true)
    }

    fun isXmlFile(mimeType: String): Boolean {
        when(mimeType.toLowerCase()) {
            "application/xml",
            "text/xml"
                -> return true
            else
                -> return false
        }
    }


    // taken from http://filext.com/faq/office_mime_types.php
    fun isMicrosoftOfficeFile(mimeType: String): Boolean {
        return isMicrosoftWordFile(mimeType) || isMicrosoftExcelFile(mimeType) || isMicrosoftPowerPointFile(mimeType)
    }

    fun isMicrosoftWordFile(mimeType: String): Boolean {
        return isMicrosoftWordDocument(mimeType) || isMicrosoftWordTemplate(mimeType)
    }

    fun isMicrosoftWordDocument(mimeType: String): Boolean {
        when(mimeType.toLowerCase()) {
            "application/msword",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
            "application/vnd.ms-word.document.macroEnabled.12"
                -> return true
            else
                -> return false
        }
    }

    fun isMicrosoftWordTemplate(mimeType: String): Boolean {
        when(mimeType.toLowerCase()) {
            "application/vnd.openxmlformats-officedocument.wordprocessingml.template",
            "application/vnd.ms-word.template.macroEnabled.12"
                -> return true
            else
                -> return false
        }
    }

    fun isMicrosoftExcelFile(mimeType: String): Boolean {
        return isMicrosoftExcelSheet(mimeType) || isMicrosoftExcelTemplate(mimeType)
    }

    fun isMicrosoftExcelSheet(mimeType: String): Boolean {
        when(mimeType.toLowerCase()) {
            "application/vnd.ms-excel",
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
            "application/vnd.ms-excel.sheet.macroEnabled.12",
            "application/vnd.ms-excel.sheet.binary.macroEnabled.12"
                -> return true
            else
                -> return false
        }
    }

    fun isMicrosoftExcelTemplate(mimeType: String): Boolean {
        when(mimeType.toLowerCase()) {
            "application/vnd.openxmlformats-officedocument.spreadsheetml.template",
            "application/vnd.ms-excel.template.macroEnabled.12"
                -> return true
            else
                -> return false
        }
    }

    fun isMicrosoftPowerPointFile(mimeType: String): Boolean {
        return isMicrosoftPowerPointPresentation(mimeType) || isMicrosoftPowerPointTemplate(mimeType) || isMicrosoftPowerPointSlideshow(mimeType)
    }

    fun isMicrosoftPowerPointPresentation(mimeType: String): Boolean {
        when(mimeType.toLowerCase()) {
            "application/vnd.ms-powerpoint",
            "application/vnd.openxmlformats-officedocument.presentationml.presentation",
            "application/vnd.ms-powerpoint.presentation.macroEnabled.12"
                -> return true
            else
                -> return false
        }
    }

    fun isMicrosoftPowerPointTemplate(mimeType: String): Boolean {
        when(mimeType.toLowerCase()) {
            "application/vnd.openxmlformats-officedocument.presentationml.template",
            "application/vnd.ms-powerpoint.template.macroEnabled.12"
                -> return true
            else
                -> return false
        }
    }

    fun isMicrosoftPowerPointSlideshow(mimeType: String): Boolean {
        when(mimeType.toLowerCase()) {
            "application/vnd.openxmlformats-officedocument.presentationml.slideshow",
            "application/vnd.ms-powerpoint.slideshow.macroEnabled.12"
                -> return true
            else
                -> return false
        }
    }


    fun isOpenOfficeFile(mimeType: String): Boolean {
        return isOpenOfficeWriterFile(mimeType) || isOpenOfficeCalcFile(mimeType) || isOpenOfficeImpressFile(mimeType) || isOtherOpenOfficeFile(mimeType)
    }

    fun isOpenOfficeWriterFile(mimeType: String): Boolean {
        return isOpenOfficeWriterDocument(mimeType) || isOpenOfficeWriterTemplate(mimeType)
    }

    fun isOpenOfficeWriterDocument(mimeType: String): Boolean {
        when(mimeType.toLowerCase()) {
            "application/vnd.oasis.opendocument.text",
            "application/vnd.sun.xml.writer",
            "application/vnd.stardivision.writer",
            "application/x-starwriter"
                -> return true
            else
                -> return false
        }
    }

    fun isOpenOfficeWriterTemplate(mimeType: String): Boolean {
        when(mimeType.toLowerCase()) {
            "application/vnd.oasis.opendocument.text-template",
            "application/vnd.sun.xml.writer.template"
                -> return true
            else
                -> return false
        }
    }

    fun isOpenOfficeCalcFile(mimeType: String): Boolean {
        return isOpenOfficeCalcSheet(mimeType) || isOpenOfficeCalcTemplate(mimeType)
    }

    fun isOpenOfficeCalcSheet(mimeType: String): Boolean {
        when(mimeType.toLowerCase()) {
            "application/vnd.oasis.opendocument.spreadsheet",
            "application/vnd.sun.xml.calc",
            "application/vnd.stardivision.calc",
            "application/x-starcalc"
                -> return true
            else
                -> return false
        }
    }

    fun isOpenOfficeCalcTemplate(mimeType: String): Boolean {
        when(mimeType.toLowerCase()) {
            "application/vnd.oasis.opendocument.spreadsheet-template",
            "application/vnd.sun.xml.calc.template"
                -> return true
            else
                -> return false
        }
    }

    fun isOpenOfficeImpressFile(mimeType: String): Boolean {
        return isOpenOfficeImpressPresentation(mimeType) || isOpenOfficeImpressTemplate(mimeType)
    }

    fun isOpenOfficeImpressPresentation(mimeType: String): Boolean {
        when(mimeType.toLowerCase()) {
            "application/vnd.oasis.opendocument.presentation",
            "application/vnd.sun.xml.impress",
            "application/vnd.stardivision.impress",
            "application/vnd.stardivision.impress-packed",
            "application/x-starimpress"
                -> return true
            else
                -> return false
        }
    }

    fun isOpenOfficeImpressTemplate(mimeType: String): Boolean {
        when(mimeType.toLowerCase()) {
            "application/vnd.oasis.opendocument.presentation-template",
            "application/vnd.sun.xml.impress.template"
                -> return true
            else
                -> return false
        }
    }

    fun isOtherOpenOfficeFile(mimeType: String): Boolean {
        // see https://www.openoffice.org/framework/documentation/mimetypes/mimetypes.html
        when(mimeType.toLowerCase()) {
            // OpenOffice 2.0 and newer
            "application/vnd.oasis.opendocument.graphics",
            "application/vnd.oasis.opendocument.graphics-template",
            "application/vnd.oasis.opendocument.chart",
            "application/vnd.oasis.opendocument.formula",
            "application/vnd.oasis.opendocument.database",
            "application/vnd.oasis.opendocument.image",
            "application/vnd.oasis.opendocument.text-master",
            "application/vnd.oasis.opendocument.text-web",
            "application/vnd.openofficeorg.extension",
            // StarOffice 6.0 / OpenOffice 1.0
            "application/vnd.sun.xml.draw",
            "application/vnd.sun.xml.draw.template",
            "application/vnd.sun.xml.writer.global",
            "application/vnd.sun.xml.math",
            // StarOffice 5.0
            "application/vnd.stardivision.draw",
            "application/vnd.stardivision.math",
            "application/vnd.stardivision.chart",
            "application/vnd.stardivision.mail",
            "application/vnd.stardivision.writer-global",
            // StarOffice 4.0
            "application/x-stardraw",
            "application/x-starmath",
            "application/x-starchart"
                -> return true
            else
                -> return false
        }
    }

}