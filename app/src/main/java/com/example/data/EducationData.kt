package com.example.data

data class Course(
    val id: String,
    val titleFr: String,
    val titleAr: String,
    val category: String,
    val descriptionFr: String,
    val descriptionAr: String,
    val videoUrl: String = "https://example.com/video.mp4",
    val videoDuration: String = "8:45",
    val pdfContentFr: String,
    val pdfContentAr: String,
    val quizQuestions: List<QuizQuestion>
)

data class QuizQuestion(
    val questionFr: String,
    val questionAr: String,
    val optionsFr: List<String>,
    val optionsAr: List<String>,
    val correctAnswerIndex: Int
)

data class LibraryItem(
    val id: String,
    val titleFr: String,
    val titleAr: String,
    val category: String, // "Fiche", "Flashcard", "Livre"
    val descriptionFr: String,
    val descriptionAr: String,
    val pdfDownloadUrl: String,
    val contentFr: String = "",
    val contentAr: String = "",
    val flashcards: List<Pair<String, String>> = emptyList() // Front (FR), Back (AR)
)

data class Story(
    val id: String,
    val titleFr: String,
    val titleAr: String,
    val category: String, // "Contes classiques", "Fables", "Histoires éducatives", "Contes modernes"
    val audioDuration: String,
    val contentFr: String,
    val contentAr: String,
    val vocabulary: List<Pair<String, String>>, // FR to AR
    val quizQuestions: List<QuizQuestion>
)

data class GameInfo(
    val id: String,
    val titleFr: String,
    val titleAr: String,
    val descriptionFr: String,
    val descriptionAr: String,
    val icon: String,
    val xpReward: Int = 100
)

object EducationData {
    val courses = listOf(
        Course(
            id = "1",
            titleFr = "Le Passé Composé",
            titleAr = "الماضي المركب",
            category = "Conjugaison",
            descriptionFr = "Apprenez à conjuguer les verbes au passé composé avec l'auxiliaire être et avoir.",
            descriptionAr = "تعلم كيفية تصريف الأفعال في الماضي المركب مع الفعلين المساعدين être و avoir.",
            pdfContentFr = "Le passé composé exprime une action passée et terminée. Il se forme avec l'auxiliaire 'avoir' ou 'être' au présent suivi du participe passé.\n\nExemples :\n- Parler : J'ai parlé, tu as parlé, il/elle a parlé, nous avons parlé, vous avez parlé, ils/elles ont parlé.\n- Aller (avec être) : Je suis allé(e), tu es allé(e), il est allé, elle est allée, nous sommes allé(e)s, vous êtes allé(e)s, ils sont allés, elles sont allées.\n\nAttention : Avec l'auxiliaire 'être', le participe passé s'accorde en genre (e/es) et en nombre (s/es) avec le sujet !",
            pdfContentAr = "يعبر الماضي المركب عن حدث ماضٍ وانتهى. يتكون من الفعل المساعد 'avoir' أو 'être' في الحاضر متبوعاً باسم المفعول (participe passé).\n\nأمثلة:\n- يتحدث (Parler): لقد تحدثت، إلخ.\n- يذهب (Aller) (مع être): لقد ذهبت، إلخ.\n\nتنبيه: مع الفعل المساعد 'être'، يتطابق اسم المفعول في الجنس والعدد مع الفاعل!",
            quizQuestions = listOf(
                QuizQuestion(
                    questionFr = "Choisissez l'auxiliaire correct : Hier, je ___ mangé une pomme.",
                    questionAr = "اختر الفعل المساعد الصحيح: البارحة، أكلت تفاحة.",
                    optionsFr = listOf("suis", "ai", "es", "as"),
                    optionsAr = listOf("suis", "ai", "es", "as"),
                    correctAnswerIndex = 1
                ),
                QuizQuestion(
                    questionFr = "Avec quel auxiliaire s'accorde toujours le participe passé ?",
                    questionAr = "مع أي فعل مساعد يتطابق اسم المفعول دائماً؟",
                    optionsFr = listOf("Avoir", "Être", "Les deux", "Aucun"),
                    optionsAr = listOf("Avoir", "Être", "كلاهما", "لا أحد"),
                    correctAnswerIndex = 1
                ),
                QuizQuestion(
                    questionFr = "Quelle est la conjugaison correcte : Elle ___ partie.",
                    questionAr = "ما هو التصريف الصحيح: هي رحلت.",
                    optionsFr = listOf("est", "a", "es", "ai"),
                    optionsAr = listOf("est", "a", "es", "ai"),
                    correctAnswerIndex = 0
                )
            )
        ),
        Course(
            id = "2",
            titleFr = "L'Accord du Participe Passé",
            titleAr = "تطابق اسم المفعول",
            category = "Grammaire",
            descriptionFr = "Maîtrisez les règles complexes d'accord du participe passé.",
            descriptionAr = "أتقن قواعد التطابق المعقدة لاسم المفعول في اللغة الفرنسية.",
            pdfContentFr = "1. Avec l'auxiliaire 'être', le participe passé s'accorde toujours avec le sujet.\n- Exemple : Elles sont arrivées.\n\n2. Avec l'auxiliaire 'avoir', le participe passé ne s'accorde jamais avec le sujet. Cependant, il s'accorde avec le COD (Complément d'Objet Direct) s'il est placé AVANT le verbe.\n- Exemple : J'ai mangé les pommes -> Les pommes que j'ai mangées.",
            pdfContentAr = "1. مع الفعل المساعد 'être'، يتطابق اسم المفعول دائماً مع الفاعل.\n- مثال: هن وصلن (Elles sont arrivées).\n\n2. مع الفعل المساعد 'avoir'، لا يتطابق اسم المفعول أبداً مع الفاعل. ومع ذلك، فإنه يتطابق مع المفعول به المباشر (COD) إذا وضع قبل الفعل.\n- مثال: أكلت التفاحات -> التفاحات التي أكلتها.",
            quizQuestions = listOf(
                QuizQuestion(
                    questionFr = "Complétez : Les fleurs qu'il a ___ sont belles.",
                    questionAr = "أكمل الجملة: الأزهار التي قطفها جميلة.",
                    optionsFr = listOf("cueilli", "cueillies", "cueillie", "cueillis"),
                    optionsAr = listOf("cueilli", "cueillies", "cueillie", "cueillis"),
                    correctAnswerIndex = 1
                ),
                QuizQuestion(
                    questionFr = "Complétez : Ils sont ___.",
                    questionAr = "أكمل الجملة: هم رحلوا.",
                    optionsFr = listOf("parti", "partis", "partie", "parties"),
                    optionsAr = listOf("parti", "partis", "partie", "parties"),
                    correctAnswerIndex = 1
                )
            )
        ),
        Course(
            id = "3",
            titleFr = "Les Accents en Français",
            titleAr = "النبرات في الفرنسية",
            category = "Orthographe",
            descriptionFr = "Comprendre l'accent aigu, grave, circonflexe et le tréma.",
            descriptionAr = "فهم النبرة الحادة، الغليظة، المنعطفة والنقطتين فوق الحرف.",
            pdfContentFr = "Les accents changent la prononciation ou le sens des mots :\n- L'accent aigu (é) : seulement sur le e. Exemple : école.\n- L'accent grave (à, è, ù) : Exemple : mère, où.\n- L'accent circonflexe (â, ê, î, ô, û) : souvent pour remplacer un 's' historique. Exemple : forêt, hôtel.\n- Le tréma (ë, ï, ü) : indique qu'il faut prononcer deux voyelles séparément. Exemple : naïf.",
            pdfContentAr = "تغير النبرات طريقة النطق أو معنى الكلمات:\n- النبرة الحادة (é): تقع فقط على حرف e. مثال: مدرسة (école).\n- النبرة الغليظة (à, è, ù): مثال: أم (mère)، أين (où).\n- النبرة المنعطفة (â, ê, î, ô, û): غالباً ما تعوض حرف s تاريخي. مثال: غابة (forêt).\n- النقطتان فوق الحرف (ë, ï, ü): تشير إلى وجوب نطق متحركين منفصلين. مثال: ساذج (naïf).",
            quizQuestions = listOf(
                QuizQuestion(
                    questionFr = "Quel accent trouve-t-on dans le mot 'forêt' ?",
                    questionAr = "ما هي النبرة الموجودة في كلمة 'forêt'؟",
                    optionsFr = listOf("Accent aigu", "Accent grave", "Accent circonflexe", "Tréma"),
                    optionsAr = listOf("Nébré hāda", "Nébré ghalīza", "Nébré mon'atifa", "Tréma"),
                    correctAnswerIndex = 2
                )
            )
        ),
        Course(
            id = "4",
            titleFr = "La Famille et les Amis",
            titleAr = "العائلة والأصدقاء",
            category = "Vocabulaire",
            descriptionFr = "Apprenez les mots essentiels pour présenter votre famille.",
            descriptionAr = "تعلم الكلمات الأساسية لتقديم عائلتك.",
            pdfContentFr = "Vocabulaire de la famille :\n- Le père (الأب) / La mère (الأم)\n- Le frère (الأخ) / La sœur (الأخت)\n- Le grand-père (الجد) / La grand-mère (الجدة)\n- L'oncle (العم/الخال) / La tante (العمّة/الخالة)\n- Le cousin (ابن العم/الخال) / La cousine (ابنة العم/الخال)",
            pdfContentAr = "مفردات العائلة:\n- الأب (Le père) / الأم (La mère)\n- الأخ (Le frère) / الأخت (La sœur)\n- الجد (Le grand-père) / الجدة (La grand-mère)",
            quizQuestions = listOf(
                QuizQuestion(
                    questionFr = "Comment dit-on 'الأخت' en français ?",
                    questionAr = "كيف نقول 'الأخت' باللغة الفرنسية؟",
                    optionsFr = listOf("La mère", "La sœur", "La tante", "La cousine"),
                    optionsAr = listOf("La mère", "La sœur", "La tante", "La cousine"),
                    correctAnswerIndex = 1
                )
            )
        )
    )

    val libraryItems = listOf(
        LibraryItem(
            id = "lib_1",
            titleFr = "Vocabulaire Essentiel Illustré",
            titleAr = "المفردات الأساسية المصورة",
            category = "Fiche",
            descriptionFr = "Une fiche PDF récapitulative de plus de 100 mots essentiels.",
            descriptionAr = "بطاقة ملخصة لأكثر من 100 كلمة أساسية بالفرنسية.",
            pdfDownloadUrl = "https://example.com/vocab.pdf",
            contentFr = "Bonjour ! Voici la liste des mots à connaître par cœur :\n- L'école (المدرسة)\n- Le cahier (الدفتر)\n- Le stylo (القلم)\n- Le livre (الكتاب)\n- L'élève (التلميذ)\n- L'enseignant (المعلم)\n- La classe (القسم)",
            contentAr = "مرحباً! إليك قائمة الكلمات الواجب حفظها عن ظهر قلب:\n- المدرسة (L'école)\n- الدفتر (Le cahier)\n- القلم (Le stylo)"
        ),
        LibraryItem(
            id = "lib_2",
            titleFr = "Flashcards de Conjugaison",
            titleAr = "بطاقات استذكار لتصريف الأفعال",
            category = "Flashcard",
            descriptionFr = "Apprenez rapidement avec nos cartes interactives recto-verso.",
            descriptionAr = "تعلم بسرعة مع بطاقاتنا التفاعلية ذات الوجهين.",
            pdfDownloadUrl = "https://example.com/flash_conjugation.pdf",
            flashcards = listOf(
                "Je suis (Présent)" to "أنا أكون",
                "Tu as (Présent)" to "أنت تملك",
                "Il fera (Futur)" to "هو سيفعل",
                "Nous mangions (Imparfait)" to "كنا نأكل",
                "Ils sont partis (Passé composé)" to "هم رحلوا"
            )
        )
    )

    val stories = listOf(
        Story(
            id = "story_1",
            titleFr = "Le Petit Lion Courageux",
            titleAr = "الأسد الصغير الشجاع",
            category = "Histoires éducatives",
            audioDuration = "3:15",
            contentFr = "Il était une fois, dans la savane africaine, un tout petit lion nommé Léo. Contrairement aux autres lions, Léo avait peur du bruit du tonnerre. Un jour, une tempête éclata. Tous les animaux couraient dans tous les sens. Léo vit un petit oiseau blessé, coincé sous une branche. Malgré sa peur du tonnerre, Léo courut l'aider, souleva la branche et sauva l'oiseau. Depuis ce jour, tout le monde l'appela le Petit Lion Courageux.",
            contentAr = "كان يا مكان، في السافانا الإفريقية، أسد صغير جداً يدعى ليو. على عكس الأسود الأخرى، كان ليو يخاف من صوت الرعد. ذات يوم، اندلعت عاصفة. كانت جميع الحيوانات تركض في كل اتجاه. رأى ليو طائراً صغيراً مصاباً عالقاً تحت غصن شجرة. على الرغم من خوفه من الرعد، ركض ليو لمساعدته، ورفع الغصن وأنقذ الطائر. ومنذ ذلك اليوم، أصبح الجميع ينادونه بالأسد الصغير الشجاع.",
            vocabulary = listOf(
                "Lion" to "أسد",
                "Courageux" to "شجاع",
                "Tonnerre" to "رعد",
                "Tempête" to "عاصفة",
                "Oiseau" to "طائر",
                "Sauver" to "إنقاذ"
            ),
            quizQuestions = listOf(
                QuizQuestion(
                    questionFr = "De quoi Léo avait-il peur ?",
                    questionAr = "مماذا كان يخاف ليو؟",
                    optionsFr = listOf("De l'eau", "Du tonnerre", "Des serpents", "De l'ombre"),
                    optionsAr = listOf("من الماء", "من الرعد", "من الثعابين", "من الظل"),
                    correctAnswerIndex = 1
                ),
                QuizQuestion(
                    questionFr = "Quel animal Léo a-t-il sauvé ?",
                    questionAr = "أي حيوان أنقذه ليو؟",
                    optionsFr = listOf("Un oiseau", "Un singe", "Une tortue", "Un lapin"),
                    optionsAr = listOf("طائر", "قرد", "سلحفاة", "أرنب"),
                    correctAnswerIndex = 0
                )
            )
        )
    )

    val gamesList = listOf(
        GameInfo(
            id = "game_1",
            titleFr = "Décodage & Alphabet",
            titleAr = "فك التشفير والحروف",
            descriptionFr = "Assemblez les lettres pour former des mots de vocabulaire.",
            descriptionAr = "جمع الحروف لتشكيل كلمات المفردات الأساسية.",
            icon = "🔤"
        ),
        GameInfo(
            id = "game_2",
            titleFr = "Défi de Vocabulaire",
            titleAr = "تحدي المفردات",
            descriptionFr = "Associez les mots français à leur traduction arabe avant la fin du temps.",
            descriptionAr = "طابق الكلمات الفرنسية بترجمتها العربية قبل انتهاء الوقت.",
            icon = "⚡"
        ),
        GameInfo(
            id = "game_3",
            titleFr = "Jeu de Mémoire",
            titleAr = "لعبة الذاكرة",
            descriptionFr = "Retrouvez les paires d'images et de mots identiques.",
            descriptionAr = "اعثر على أزواج الصور والكلمات المتطابقة.",
            icon = "🧠"
        )
    )
}
