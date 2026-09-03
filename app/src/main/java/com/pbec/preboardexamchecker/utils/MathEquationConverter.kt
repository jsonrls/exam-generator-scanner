package com.pbec.preboardexamchecker.utils

/** A piece of source text rendered either as normal prose or as KaTeX. */
data class MathRenderSegment(
    val source: String,
    val latex: String? = null,
    val displayMode: Boolean = false,
) {
    val isMath: Boolean get() = latex != null
}

/**
 * Converts notation commonly found in imported question-bank spreadsheets into KaTeX.
 *
 * Prose and mathematics stay in separate segments. Rendering an entire sentence in math mode
 * removes normal spaces and italicizes every letter, which was the main reason imported
 * Mathematics questions previously looked broken. Explicit LaTeX delimiters are supported,
 * while un-delimited spreadsheet notation is detected conservatively.
 */
object MathEquationConverter {

    private val EXCLUDED_FRACTION_UNITS = setOf(
        "g/ml", "km/h", "m/s", "n/m^2", "j/s", "w/m^2", "ft/s", "lb/ft",
        "lbf/ft^2", "lb/gal", "oz/gal", "gal/min", "btu/hr", "kcal/hr",
        "rev/s", "n/kg", "kg/m^3", "kg/s", "m/s^2", "n/m", "pa/s",
        "w/m", "j/kg", "m^3/s", "km/s", "rad/s", "rev/min", "g/cm^3",
        "kg/l", "lb/ft^3", "gal/s", "mol/l", "mol/m^3", "n/mm^2",
        "kw/m^2", "m/min", "km/min", "g/l", "j/m^2", "w/m^3", "v/m",
        "a/m^2", "cd/m^2", "ft/min", "lb/in^2", "oz/ft^2",
        "c/m^2", "f/m", "h/m", "s/m", "ω/m", "ω/m^2", "a/m",
        "wb/m^2", "v/a", "bit/s", "byte/s", "kbit/s", "mbit/s",
        "gbit/s", "hz/v", "mho/m", "mho/cm", "a/v", "μf/m",
        "nh/m", "t/m", "v/cm", "db/m", "baud/s", "flops/s", "pf/m",
        "ma/cm^2", "lm/w", "bit/m", "kg/cm", "kj/s", "btu/lb", "ft/sec",
        "r/min", "m/se", "cu/m", "m/sec", "rad/sec", "rev/sec", "kg/cu.m",
        "m/sec^2", "kcal/s", "btu/s", "hp/s", "lb/cu.ft", "g/cu.cm",
        "kg/cu", "kg/cm^2", "in/s", "cm/hr", "mi/hr", "cm/sec", "m/hr"
    ).map { it.lowercase() }.toSet()

    private val CHEMICAL_FORMULAS = setOf(
        "NO2", "NO3", "SO4", "CO3", "PO4", "OH", "NH4", "H2O",
        "Mg(NO2)2", "Mg(NO3)2", "CaSO4", "NaCl", "KOH", "H2SO4",
        "HNO3", "HCl", "Na2CO3", "Ca(OH)2", "MgNO2", "CO2", "Mg", "NO", "CO"
    ).map { it.lowercase() }.toSet()

    private val FUNCTIONS = setOf(
        "sin", "cos", "tan", "cot", "sec", "csc",
        "arcsin", "arccos", "arctan", "sinh", "cosh", "tanh",
        "log", "ln", "exp", "lim", "det"
    )

    private val CONSTANT_WORDS = setOf(
        "pi", "theta", "alpha", "beta", "gamma", "delta", "lambda", "phi",
        "omega", "infinity"
    )

    private val UNIT_WORDS = setOf(
        "m", "s", "sec", "min", "h", "hr", "kg", "g", "mg", "km", "cm", "mm",
        "ft", "in", "lb", "lbf", "oz", "gal", "l", "mol", "rad", "rev", "rpm",
        "n", "pa", "j", "w", "kw", "v", "a", "c", "f", "hz", "wb", "btu",
        "kcal", "hp", "bit", "byte", "baud", "flops", "mho", "lm", "db", "pf", "ma"
    )

    private val PROSE_WORDS = setOf(
        "an", "and", "are", "as", "at", "be", "by", "find", "for", "from",
        "given", "has", "if", "in", "is", "it", "of", "on", "or", "some", "than",
        "that", "the", "then", "to", "value", "what", "when", "where", "which",
        "with", "approaches", "equal", "equals", "respectively", "ii", "iii", "iv"
    )

    private val VARIABLE_PRODUCT = Regex("(?i)(?:d[abcijklmnpqrstuvwxyz]{1,5}|[abcijklmnpqrstuvwxyz]{2,5})")
    private val COEFFICIENT_MONOMIAL = Regex("(?i)\\d+(?:\\.\\d+)?[a-z]{1,5}")
    private val UNDERSCORE_REGEX = Regex("_+")
    private val CURRENCY_ONLY_REGEX = Regex("""[P$]\s*[0-9,]+(?:\.[0-9]{1,2})?""")
    private val CURRENCY_ANYWHERE_REGEX = Regex("""[P$]\s*[0-9,]+(?:\.[0-9]{1,2})?\b""")
    private const val UNIT_SLASH_PLACEHOLDER = '\uE000'

    private val SUPERSCRIPTS = mapOf(
        '⁰' to '0', '¹' to '1', '²' to '2', '³' to '3', '⁴' to '4',
        '⁵' to '5', '⁶' to '6', '⁷' to '7', '⁸' to '8', '⁹' to '9',
        '⁺' to '+', '⁻' to '-', '⁼' to '=', '⁽' to '(', '⁾' to ')',
        'ⁿ' to 'n', 'ⁱ' to 'i', 'ˣ' to 'x'
    )

    private val SUBSCRIPTS = mapOf(
        '₀' to '0', '₁' to '1', '₂' to '2', '₃' to '3', '₄' to '4',
        '₅' to '5', '₆' to '6', '₇' to '7', '₈' to '8', '₉' to '9',
        '₊' to '+', '₋' to '-', '₌' to '=', '₍' to '(', '₎' to ')',
        'ₐ' to 'a', 'ₑ' to 'e', 'ₕ' to 'h', 'ᵢ' to 'i', 'ⱼ' to 'j',
        'ₖ' to 'k', 'ₗ' to 'l', 'ₘ' to 'm', 'ₙ' to 'n', 'ₒ' to 'o',
        'ₚ' to 'p', 'ᵣ' to 'r', 'ₛ' to 's', 'ₜ' to 't', 'ᵤ' to 'u',
        'ᵥ' to 'v', 'ₓ' to 'x'
    )

    private enum class TokenKind {
        WORD, NUMBER, COMMAND, SPACE, OPERATOR, OPEN, CLOSE, COMMA, PERIOD, OTHER
    }

    private data class Token(
        val text: String,
        val start: Int,
        val end: Int,
        val kind: TokenKind,
    )

    private data class TextRange(val start: Int, val end: Int)

    fun isUnderscoreOnly(text: String): Boolean = UNDERSCORE_REGEX.matches(text.trim())

    /** Returns prose/math segments suitable for mixed inline rendering. */
    fun renderSegments(text: String): List<MathRenderSegment> {
        if (text.isEmpty()) return listOf(MathRenderSegment(source = text))

        val result = mutableListOf<MathRenderSegment>()
        for (part in splitExplicitMath(text)) {
            if (part.isMath) {
                result += part.copy(latex = normalizeExplicitLatex(part.latex.orEmpty()))
            } else {
                result += detectImplicitMath(part.source)
            }
        }
        return mergeAdjacentText(result)
    }

    /** Legacy API used by PDF export; input should be one formula segment. */
    fun convertEquationTextToLaTeX(text: String): String {
        val trimmed = text.trim()
        if (trimmed.isBlank() || isUnderscoreOnly(trimmed) || CURRENCY_ONLY_REGEX.matches(trimmed)) {
            return trimmed
        }

        val explicit = unwrapExplicitMath(trimmed)
        return if (explicit != null) normalizeExplicitLatex(explicit.first)
        else normalizeMathExpression(trimmed)
    }

    fun containsMathSyntax(text: String): Boolean = renderSegments(text).any { it.isMath }

    fun isCurrencyValue(text: String): Boolean = CURRENCY_ANYWHERE_REGEX.containsMatchIn(text)

    fun isChemicalFormula(text: String): Boolean {
        val compact = normalizeChemicalForDetection(text)
        if (compact.isBlank()) return false
        if (compact.lowercase() in CHEMICAL_FORMULAS) return true

        val element = "(?:[A-Z][a-z]?\\d*)"
        val group = "(?:\\((?:$element)+\\)\\d*)"
        return Regex("^(?:(?:$element)|(?:$group)){2,}$").matches(compact)
    }

    /** Legacy pair format retained for the PDF exporter. */
    fun splitMathParts(text: String): List<Pair<String, Boolean>> =
        renderSegments(text).map { it.source to it.isMath }

    fun getExcludedFractionUnits(): Set<String> = EXCLUDED_FRACTION_UNITS

    private fun splitExplicitMath(text: String): List<MathRenderSegment> {
        val parts = mutableListOf<MathRenderSegment>()
        var textStart = 0
        var index = 0

        while (index < text.length) {
            val delimiter = when {
                text.startsWith("$$", index) && !isEscaped(text, index) -> Triple("$$", "$$", true)
                text.startsWith("\\[", index) && !isEscaped(text, index) -> Triple("\\[", "\\]", true)
                text.startsWith("\\(", index) && !isEscaped(text, index) -> Triple("\\(", "\\)", false)
                text[index] == '$' && !isEscaped(text, index) -> Triple("$", "$", false)
                else -> null
            }

            if (delimiter == null) {
                index++
                continue
            }

            val close = findUnescaped(text, delimiter.second, index + delimiter.first.length)
            if (close < 0) {
                index += delimiter.first.length
                continue
            }

            val mathSource = text.substring(index + delimiter.first.length, close)
            if (mathSource.isBlank()) {
                index = close + delimiter.second.length
                continue
            }

            if (index > textStart) parts += MathRenderSegment(source = text.substring(textStart, index))
            parts += MathRenderSegment(
                source = mathSource,
                latex = mathSource,
                displayMode = delimiter.third,
            )

            index = close + delimiter.second.length
            textStart = index
        }

        if (textStart < text.length) parts += MathRenderSegment(source = text.substring(textStart))
        if (parts.isEmpty()) parts += MathRenderSegment(source = text)
        return parts
    }

    private fun unwrapExplicitMath(text: String): Pair<String, Boolean>? {
        val delimiters = listOf(
            Triple("$$", "$$", true),
            Triple("\\[", "\\]", true),
            Triple("\\(", "\\)", false),
            Triple("$", "$", false),
        )
        for ((open, close, display) in delimiters) {
            if (text.startsWith(open) && text.endsWith(close) && text.length >= open.length + close.length) {
                return text.substring(open.length, text.length - close.length) to display
            }
        }
        return null
    }

    private fun findUnescaped(text: String, delimiter: String, fromIndex: Int): Int {
        var index = text.indexOf(delimiter, fromIndex)
        while (index >= 0 && isEscaped(text, index)) {
            index = text.indexOf(delimiter, index + delimiter.length)
        }
        return index
    }

    private fun isEscaped(text: String, index: Int): Boolean {
        var slashes = 0
        var cursor = index - 1
        while (cursor >= 0 && text[cursor] == '\\') {
            slashes++
            cursor--
        }
        return slashes % 2 == 1
    }

    private fun detectImplicitMath(text: String): List<MathRenderSegment> {
        if (text.isBlank() || isUnderscoreOnly(text) || CURRENCY_ONLY_REGEX.matches(text.trim())) {
            return listOf(MathRenderSegment(source = text))
        }
        if (isChemicalFormula(text.trim())) {
            return listOf(MathRenderSegment(source = text, latex = normalizeMathExpression(text.trim())))
        }

        val tokens = tokenize(text)
        if (tokens.isEmpty()) return listOf(MathRenderSegment(source = text))

        val ranges = mutableListOf<TextRange>()
        tokens.indices.forEach { index ->
            if (!isStrongAnchor(tokens, index)) return@forEach

            var left = index
            var right = index
            while (left > 0 && isAllowedInFormula(tokens, left - 1)) left--
            while (right + 1 < tokens.size && isAllowedInFormula(tokens, right + 1)) right++

            trimCandidate(text, tokens[left].start, tokens[right].end)?.let(ranges::add)
        }

        if (ranges.isEmpty()) return listOf(MathRenderSegment(source = text))

        val merged = ranges.sortedBy { it.start }.fold(mutableListOf<TextRange>()) { acc, range ->
            val last = acc.lastOrNull()
            if (last != null && range.start <= last.end) {
                acc[acc.lastIndex] = TextRange(last.start, maxOf(last.end, range.end))
            } else {
                acc += range
            }
            acc
        }

        val segments = mutableListOf<MathRenderSegment>()
        var cursor = 0
        for (range in merged) {
            if (range.start > cursor) segments += MathRenderSegment(source = text.substring(cursor, range.start))
            val source = text.substring(range.start, range.end)
            val latex = normalizeMathExpression(source)
            if (latex.isBlank()) segments += MathRenderSegment(source = source)
            else segments += MathRenderSegment(source = source, latex = latex)
            cursor = range.end
        }
        if (cursor < text.length) segments += MathRenderSegment(source = text.substring(cursor))
        return segments
    }

    private fun tokenize(text: String): List<Token> {
        val tokens = mutableListOf<Token>()
        var index = 0

        while (index < text.length) {
            val start = index
            val ch = text[index]
            val kind: TokenKind

            when {
                ch.isWhitespace() -> {
                    while (index < text.length && text[index].isWhitespace()) index++
                    kind = TokenKind.SPACE
                }
                ch == '\\' && index + 1 < text.length && text[index + 1].isLetter() -> {
                    index += 2
                    while (index < text.length && text[index].isLetter()) index++
                    kind = TokenKind.COMMAND
                }
                ch.isDigit() || (ch == '.' && text.getOrNull(index + 1)?.isDigit() == true) -> {
                    var containsLetter = false
                    index++
                    while (index < text.length) {
                        val current = text[index]
                        val keep = current.isLetterOrDigit() || isScriptCharacter(current) ||
                            current == '\'' || current == '′' ||
                            (current == '.' && text.getOrNull(index + 1)?.isDigit() == true) ||
                            (current == ',' && text.getOrNull(index + 1)?.isDigit() == true)
                        if (!keep) break
                        if (current.isLetter()) containsLetter = true
                        index++
                    }
                    kind = if (containsLetter) TokenKind.WORD else TokenKind.NUMBER
                }
                ch.isLetter() || isScriptCharacter(ch) -> {
                    index++
                    while (index < text.length) {
                        val current = text[index]
                        if (!(current.isLetterOrDigit() || isScriptCharacter(current) || current == '\'' || current == '′' || current == '"')) break
                        index++
                    }
                    kind = TokenKind.WORD
                }
                ch in "+-−–—×÷=/^_<>≤≥≠±·|!:" -> {
                    index++
                    kind = TokenKind.OPERATOR
                }
                ch in "([{⟨" -> {
                    index++
                    kind = TokenKind.OPEN
                }
                ch in ")]⟩}" -> {
                    index++
                    kind = TokenKind.CLOSE
                }
                ch == ',' -> {
                    index++
                    kind = TokenKind.COMMA
                }
                ch == '.' || ch == ';' || ch == '?' -> {
                    index++
                    kind = TokenKind.PERIOD
                }
                isMathSymbol(ch) || ch == '!' || ch == '%' -> {
                    index++
                    kind = TokenKind.OPERATOR
                }
                else -> {
                    index++
                    kind = TokenKind.OTHER
                }
            }
            tokens += Token(text.substring(start, index), start, index, kind)
        }
        return tokens
    }

    private fun isStrongAnchor(tokens: List<Token>, index: Int): Boolean {
        val token = tokens[index]
        return when (token.kind) {
            TokenKind.COMMAND -> true
            TokenKind.WORD -> {
                val base = baseWord(token.text)
                token.text.any(::isMathSymbol) ||
                    base in CONSTANT_WORDS ||
                    base in setOf("π", "θ", "φ", "λ", "δ", "Δ", "ω") ||
                    (base in FUNCTIONS && hasFunctionArgument(tokens, index))
            }
            TokenKind.OPERATOR -> when {
                token.text.all { it == '_' } -> hasMathNeighbor(tokens, index, -1) && hasMathNeighbor(tokens, index, 1)
                token.text == "!" -> hasMathNeighbor(tokens, index, -1)
                token.text == "^" || token.text == "_" ->
                    hasMathNeighbor(tokens, index, -1) && hasMathNeighbor(tokens, index, 1)
                token.text == "%" -> hasMathNeighbor(tokens, index, -1)
                token.text.length == 1 && isMathSymbol(token.text[0]) -> true
                else -> hasMathNeighbor(tokens, index, -1) && hasMathNeighbor(tokens, index, 1)
            }
            TokenKind.COMMA -> isCoordinateComma(tokens, index)
            else -> false
        }
    }

    private fun hasFunctionArgument(tokens: List<Token>, index: Int): Boolean {
        val next = nextNonSpace(tokens, index, 1) ?: return false
        return tokens[next].kind == TokenKind.OPEN || isMathAtom(tokens[next])
    }

    private fun hasMathNeighbor(tokens: List<Token>, index: Int, direction: Int): Boolean {
        val neighbor = nextNonSpace(tokens, index, direction) ?: return false
        if (tokens[neighbor].text == "|") {
            val beyondBar = nextNonSpace(tokens, neighbor, direction) ?: return false
            return isMathAtom(tokens[beyondBar])
        }
        return isMathAtom(tokens[neighbor]) ||
            (direction < 0 && tokens[neighbor].kind == TokenKind.CLOSE) ||
            (direction > 0 && tokens[neighbor].kind == TokenKind.OPEN)
    }

    private fun nextNonSpace(tokens: List<Token>, index: Int, direction: Int): Int? {
        var cursor = index + direction
        while (cursor in tokens.indices) {
            if (tokens[cursor].kind != TokenKind.SPACE) return cursor
            cursor += direction
        }
        return null
    }

    private fun isCoordinateComma(tokens: List<Token>, index: Int): Boolean {
        if (!hasMathNeighbor(tokens, index, -1) || !hasMathNeighbor(tokens, index, 1)) return false
        var depth = 0
        for (cursor in index - 1 downTo 0) {
            when (tokens[cursor].kind) {
                TokenKind.CLOSE -> depth++
                TokenKind.OPEN -> if (depth == 0) return true else depth--
                TokenKind.PERIOD -> if (depth == 0) return false
                else -> Unit
            }
        }
        return false
    }

    private fun isAllowedInFormula(tokens: List<Token>, index: Int): Boolean {
        val token = tokens[index]
        return when (token.kind) {
            TokenKind.SPACE, TokenKind.NUMBER, TokenKind.COMMAND,
            TokenKind.OPEN, TokenKind.CLOSE -> true
            TokenKind.WORD -> isMathWord(token.text)
            TokenKind.OPERATOR -> token.text != "%" || hasMathNeighbor(tokens, index, -1)
            TokenKind.COMMA -> isCoordinateComma(tokens, index)
            TokenKind.PERIOD, TokenKind.OTHER -> false
        }
    }

    private fun isMathAtom(token: Token): Boolean = when (token.kind) {
        TokenKind.NUMBER, TokenKind.COMMAND -> true
        TokenKind.WORD -> isMathWord(token.text)
        else -> false
    }

    private fun isMathWord(value: String): Boolean {
        val base = baseWord(value)
        if (value.any(::isScriptCharacter)) return true
        if (base in FUNCTIONS || base in CONSTANT_WORDS || base in UNIT_WORDS) return true
        if (value.any(::isMathSymbol)) return true
        if (base in PROSE_WORDS) return false
        if (base.matches(Regex("(?i)(?:sin|cos|tan|log)[a-z]"))) return true
        if (base.length == 1 && base[0].isLetter()) return true
        if (VARIABLE_PRODUCT.matches(base) || COEFFICIENT_MONOMIAL.matches(base)) return true
        return base.matches(Regex("(?i)[a-z]\\d+"))
    }

    private fun baseWord(value: String): String = buildString {
        value.forEach { ch -> if (!isScriptCharacter(ch) && ch != '\'' && ch != '′' && ch != '"') append(ch) }
    }.lowercase()

    private fun trimCandidate(text: String, rawStart: Int, rawEnd: Int): TextRange? {
        var start = rawStart
        var end = rawEnd

        while (start < end && (text[start].isWhitespace() || text[start] in ",;:")) start++
        while (end > start && (text[end - 1].isWhitespace() || text[end - 1] in ",;:.?")) end--

        val invalidLeading = setOf('=', '+', '×', '÷', '/', '<', '>')
        val invalidTrailing = invalidLeading + setOf('-', '−', '±', '·', '|', '^', '_')
        while (start < end && text[start] in invalidLeading) start++
        while (end > start && text[end - 1] in invalidTrailing) end--
        while (start < end && text[start].isWhitespace()) start++
        while (end > start && text[end - 1].isWhitespace()) end--

        if (start >= end) return null
        val candidate = text.substring(start, end)
        if (candidate.all { it.isWhitespace() || it in "_+-=/" }) return null
        return TextRange(start, end)
    }

    private fun mergeAdjacentText(segments: List<MathRenderSegment>): List<MathRenderSegment> {
        val merged = mutableListOf<MathRenderSegment>()
        for (segment in segments) {
            val last = merged.lastOrNull()
            if (!segment.isMath && last != null && !last.isMath) {
                merged[merged.lastIndex] = last.copy(source = last.source + segment.source)
            } else {
                merged += segment
            }
        }
        return merged
    }

    private fun normalizeMathExpression(source: String): String {
        var result = source.trim()
        if (result.isBlank()) return result

        result = normalizeRootPhrases(result)
        result = normalizeRoots(result)
        result = convertSlashFractions(result)
        result = normalizeUnicodeScripts(result)
        result = normalizeAsciiScripts(result)
        result = normalizeSymbols(result)
        result = normalizeFunctions(result)
        result = normalizeDerivativeQuotes(result)
        result = escapeReservedMathCharacters(result)
        return result.trim()
    }

    private fun normalizeExplicitLatex(source: String): String {
        var result = source.trim()
        result = normalizeUnicodeScripts(result)
        result = normalizeRoots(result)
        result = normalizeSymbols(result)
        return escapeReservedMathCharacters(result).trim()
    }

    private fun normalizeRootPhrases(source: String): String {
        var result = source
        val parenthesizedRoot = Regex("""(?i)\b(?:sq\.?\s*rt\.?|square\s+root|sqrt)\s*(?:of\s*)?\(([^()]*)\)""")
        result = parenthesizedRoot.replace(result) { "\\sqrt{${it.groupValues[1]}}" }

        val bracedRoot = Regex("""(?i)\b(?:sq\.?\s*rt\.?|square\s+root|sqrt)\s*(?:of\s*)?\{([^{}]*)}""")
        result = bracedRoot.replace(result) { "\\sqrt{${it.groupValues[1]}}" }

        val simpleRoot = Regex("""(?i)\b(?:square\s+root|sqrt)\s+of\s+([A-Za-z0-9.πθφλ]+)""")
        return simpleRoot.replace(result) { "\\sqrt{${it.groupValues[1]}}" }
    }

    private fun normalizeUnicodeScripts(source: String): String {
        val output = StringBuilder()
        var index = 0

        while (index < source.length) {
            val map = when {
                SUPERSCRIPTS.containsKey(source[index]) -> SUPERSCRIPTS
                SUBSCRIPTS.containsKey(source[index]) -> SUBSCRIPTS
                else -> null
            }
            if (map == null) {
                output.append(source[index++])
                continue
            }

            val marker = if (map === SUPERSCRIPTS) '^' else '_'
            val content = StringBuilder()
            while (index < source.length) {
                val converted = map[source[index]]
                if (converted != null) {
                    content.append(converted)
                    index++
                    continue
                }
                if (source[index].isWhitespace()) {
                    var next = index
                    while (next < source.length && source[next].isWhitespace()) next++
                    if (next < source.length && map.containsKey(source[next])) {
                        index = next
                        continue
                    }
                }
                break
            }
            output.append(marker).append('{').append(content).append('}')
        }
        return output.toString()
    }

    private fun normalizeAsciiScripts(source: String): String {
        val output = StringBuilder()
        var index = 0

        while (index < source.length) {
            val marker = source[index]
            if ((marker != '^' && marker != '_') || index + 1 >= source.length) {
                output.append(marker)
                index++
                continue
            }

            output.append(marker)
            var cursor = index + 1
            if (source[cursor] == '{') {
                val close = findMatching(source, cursor)
                if (close != null) {
                    output.append(source, cursor, close + 1)
                    index = close + 1
                    continue
                }
            }

            if (source[cursor] == '(') {
                val close = findMatching(source, cursor)
                if (close != null) {
                    output.append('{').append(source, cursor, close + 1).append('}')
                    index = close + 1
                    continue
                }
            }

            val valueStart = cursor
            if (source[cursor] == '+' || source[cursor] == '-') cursor++
            if (cursor < source.length && source[cursor] == '\\') {
                cursor = consumeLatexCommandRight(source, cursor)
            } else if (cursor < source.length && source[cursor].isDigit()) {
                while (cursor < source.length && (source[cursor].isDigit() || source[cursor] == '.')) cursor++
            } else if (cursor < source.length && source[cursor].isLetter()) {
                cursor++
            }

            if (cursor == valueStart || (cursor == valueStart + 1 && source[valueStart] in "+-")) {
                index++
                continue
            }
            output.append('{').append(source, valueStart, cursor).append('}')
            index = cursor
        }
        return output.toString()
    }

    private fun normalizeRoots(source: String): String {
        var result = source.replace(Regex("""√\s*(?=\{)"""), "\\\\sqrt")
        result = result.replace(Regex("""√\s*([A-Za-z0-9.]+)""")) { "\\sqrt{${it.groupValues[1]}}" }
        result = result.replace(Regex("""(?i)(?<!\\)\bsqrt\s*(?=\{)"""), "\\\\sqrt")
        return result
    }

    private fun normalizeSymbols(source: String): String {
        return source
            .replace("−", "-")
            .replace("–", "-")
            .replace("—", "-")
            .replace("×", "\\times ")
            .replace("÷", "\\div ")
            .replace("±", "\\pm ")
            .replace("·", "\\cdot ")
            .replace("≤", "\\le ")
            .replace("≥", "\\ge ")
            .replace("≠", "\\ne ")
            .replace("∞", "\\infty{}")
            .replace("π", "\\pi{}")
            .replace("θ", "\\theta{}")
            .replace("φ", "\\phi{}")
            .replace("λ", "\\lambda{}")
            .replace("δ", "\\delta{}")
            .replace("Δ", "\\Delta{}")
            .replace("ω", "\\omega{}")
            .replace("½", "\\frac{1}{2}")
            .replace("…", "\\ldots ")
            .replace("′", "'")
            .replace(Regex("""\^\s*°"""), "^{\\\\circ}")
            .replace("°", "^{\\circ}")
            .replace("~", "\\sim ")
    }

    private fun normalizeFunctions(source: String): String {
        var result = source
        result = result.replace(Regex("""(?i)(?<!\\)\barc\s+(sin|cos|tan)\b""")) {
            "\\arc${it.groupValues[1].lowercase()}"
        }
        result = result.replace(Regex("""(?i)(?<!\\)\b(sin|cos|tan)([a-z])\b""")) {
            "\\${it.groupValues[1].lowercase()} ${it.groupValues[2]}"
        }

        val names = FUNCTIONS.sortedByDescending { it.length }.joinToString("|") { Regex.escape(it) }
        result = Regex("(?i)(?<![A-Za-z\\\\])($names)(?![A-Za-z])").replace(result) {
            "\\${it.groupValues[1].lowercase()}"
        }

        val constants = mapOf(
            "infinity" to "\\infty{}", "theta" to "\\theta{}", "alpha" to "\\alpha{}",
            "beta" to "\\beta{}", "gamma" to "\\gamma{}", "delta" to "\\delta{}",
            "lambda" to "\\lambda{}", "phi" to "\\phi{}", "omega" to "\\omega{}",
            "pi" to "\\pi{}",
        )
        constants.forEach { (plain, latex) ->
            result = Regex("(?i)(?<!\\\\)\\b${Regex.escape(plain)}\\b").replace(result) { latex }
        }
        return result
    }

    private fun normalizeDerivativeQuotes(source: String): String =
        Regex("""([A-Za-z])\"""").replace(source) { "${it.groupValues[1]}''" }

    private fun escapeReservedMathCharacters(source: String): String {
        var result = source
        result = Regex("""(?<!\\)%""").replace(result, "\\\\%")
        result = Regex("""(?<!\\)#""").replace(result, "\\\\#")
        result = Regex("""(?<!\\)&""").replace(result, "\\\\&")
        result = Regex("""(?<!\\)\$""").replace(result, "\\\\$")
        return result
    }

    private fun convertSlashFractions(source: String): String {
        var result = source
        var guard = 0

        while (guard++ < 128) {
            var slash = -1
            var cursor = 0
            while (cursor < result.length) {
                if (result[cursor] == '/' && !isEscaped(result, cursor)) {
                    slash = cursor
                    break
                }
                cursor++
            }
            if (slash < 0) break

            val left = findLeftOperand(result, slash - 1)
            val right = findRightOperand(result, slash + 1)
            if (left == null || right == null) {
                result = result.substring(0, slash) + "\\slash " + result.substring(slash + 1)
                continue
            }

            val numerator = result.substring(left.start, left.end).trim()
            val denominator = result.substring(right.start, right.end).trim()
            if (isUnitFraction(numerator, denominator)) {
                val unitRate = "\\mathrm{$numerator}$UNIT_SLASH_PLACEHOLDER\\mathrm{$denominator}"
                result = result.replaceRange(left.start, right.end, unitRate)
                continue
            }

            val replacement = "\\frac{${stripOuterGrouping(numerator)}}{${stripOuterGrouping(denominator)}}"
            result = result.replaceRange(left.start, right.end, replacement)
        }
        return result.replace(UNIT_SLASH_PLACEHOLDER.toString(), "\\,/\\,")
    }

    private fun findLeftOperand(text: String, fromIndex: Int): TextRange? {
        var end = fromIndex + 1
        while (end > 0 && text[end - 1].isWhitespace()) end--
        if (end <= 0) return null

        var start = consumeAtomLeft(text, end - 1) ?: return null

        var cursor = start
        while (cursor > 0 && text[cursor - 1].isWhitespace()) cursor--
        if (cursor < start) {
            val functionStart = consumeAtomLeft(text, cursor - 1)
            if (functionStart != null && isFunctionAtom(text.substring(functionStart, cursor))) start = functionStart
        }

        while (start > 0 && !text[start - 1].isWhitespace() && isAtomEndingCharacter(text[start - 1])) {
            val extended = consumeAtomLeft(text, start - 1) ?: break
            if (extended == start) break
            start = extended
        }
        return TextRange(start, end)
    }

    private fun findRightOperand(text: String, fromIndex: Int): TextRange? {
        var start = fromIndex
        while (start < text.length && text[start].isWhitespace()) start++
        if (start >= text.length) return null

        var cursor = start
        if (text[cursor] == '+' || text[cursor] == '-' || text[cursor] == '−') cursor++
        if (cursor >= text.length) return null

        var end = consumeAtomRight(text, cursor) ?: return null

        val firstAtom = text.substring(cursor, end)
        if (isFunctionAtom(firstAtom)) {
            var argumentStart = end
            while (argumentStart < text.length && text[argumentStart].isWhitespace()) argumentStart++
            consumeAtomRight(text, argumentStart)?.let { end = it }
        }

        while (end < text.length && !text[end].isWhitespace() && isAtomStartingCharacter(text[end])) {
            val extended = consumeAtomRight(text, end) ?: break
            if (extended == end) break
            end = extended
        }
        return TextRange(start, end)
    }

    private fun consumeAtomLeft(text: String, fromIndex: Int): Int? {
        if (fromIndex !in text.indices) return null
        var cursor = fromIndex

        if (text[cursor] in ")]}") {
            val open = findMatchingBackward(text, cursor) ?: return null
            var start = open

            var groupCursor = start
            while (groupCursor > 0 && text[groupCursor - 1] == '}') {
                groupCursor = findMatchingBackward(text, groupCursor - 1) ?: break
                start = groupCursor
            }
            var commandEnd = start
            while (commandEnd > 0 && text[commandEnd - 1].isLetter()) commandEnd--
            if (commandEnd > 0 && text[commandEnd - 1] == '\\') start = commandEnd - 1

            if (start > 0 && text[start - 1] in "^_") {
                val baseStart = consumeAtomLeft(text, start - 2)
                if (baseStart != null) start = baseStart
            }
            return start
        }

        if (!isAtomEndingCharacter(text[cursor])) return null
        while (cursor >= 0 && isSimpleAtomCharacter(text[cursor])) cursor--
        var start = cursor + 1
        if (start > 0 && text[start - 1] == '\\') start--
        return start
    }

    private fun consumeAtomRight(text: String, fromIndex: Int): Int? {
        if (fromIndex !in text.indices) return null
        val ch = text[fromIndex]

        if (ch in "([{⟨") return findMatching(text, fromIndex)?.plus(1)
        if (ch == '\\' && text.getOrNull(fromIndex + 1)?.isLetter() == true) {
            return consumeLatexCommandRight(text, fromIndex)
        }
        if (!isAtomStartingCharacter(ch)) return null

        var cursor = fromIndex
        while (cursor < text.length && isSimpleAtomCharacter(text[cursor])) cursor++

        while (cursor < text.length && text[cursor] in "^_") {
            cursor++
            if (cursor >= text.length) break
            if (text[cursor] == '{' || text[cursor] == '(') {
                val close = findMatching(text, cursor) ?: break
                cursor = close + 1
            } else {
                if (text[cursor] == '+' || text[cursor] == '-') cursor++
                if (cursor < text.length && text[cursor].isDigit()) {
                    while (cursor < text.length && text[cursor].isDigit()) cursor++
                } else if (cursor < text.length) cursor++
            }
        }
        return cursor
    }

    private fun consumeLatexCommandRight(text: String, fromIndex: Int): Int {
        var cursor = fromIndex + 1
        while (cursor < text.length && text[cursor].isLetter()) cursor++
        while (cursor < text.length) {
            if (text[cursor] in "^_") {
                cursor++
                if (cursor < text.length && text[cursor] == '{') {
                    val close = findMatching(text, cursor) ?: return cursor
                    cursor = close + 1
                    continue
                }
            }
            if (cursor < text.length && text[cursor] == '{') {
                val close = findMatching(text, cursor) ?: return cursor
                cursor = close + 1
                continue
            }
            break
        }
        return cursor
    }

    private fun findMatching(text: String, openIndex: Int): Int? {
        val open = text.getOrNull(openIndex) ?: return null
        val close = when (open) {
            '(' -> ')'
            '[' -> ']'
            '{' -> '}'
            '⟨' -> '⟩'
            else -> return null
        }
        var depth = 0
        for (index in openIndex until text.length) {
            if (text[index] == open) depth++
            if (text[index] == close && --depth == 0) return index
        }
        return null
    }

    private fun findMatchingBackward(text: String, closeIndex: Int): Int? {
        val close = text.getOrNull(closeIndex) ?: return null
        val open = when (close) {
            ')' -> '('
            ']' -> '['
            '}' -> '{'
            '⟩' -> '⟨'
            else -> return null
        }
        var depth = 0
        for (index in closeIndex downTo 0) {
            if (text[index] == close) depth++
            if (text[index] == open && --depth == 0) return index
        }
        return null
    }

    private fun stripOuterGrouping(value: String): String {
        val trimmed = value.trim()
        if (trimmed.length < 2 || trimmed.first() !in "([{⟨") return trimmed
        val close = findMatching(trimmed, 0) ?: return trimmed
        return if (close == trimmed.lastIndex) trimmed.substring(1, trimmed.lastIndex) else trimmed
    }

    private fun isFunctionAtom(value: String): Boolean {
        val name = value
            .replace(Regex("""\\[A-Za-z]+""")) { it.value.drop(1) }
            .filter { it.isLetter() }
            .lowercase()
        return name in FUNCTIONS
    }

    private fun isUnitFraction(numerator: String, denominator: String): Boolean {
        val left = normalizeUnitForDetection(numerator)
        val right = normalizeUnitForDetection(denominator)
        if ("$left/$right" in EXCLUDED_FRACTION_UNITS) return true
        val baseLeft = left.replace(Regex("\\^\\d+"), "")
        val baseRight = right.replace(Regex("\\^\\d+"), "")
        return "$baseLeft/$baseRight" in EXCLUDED_FRACTION_UNITS
    }

    private fun normalizeUnitForDetection(value: String): String {
        val output = StringBuilder()
        value.trim().forEach { ch ->
            when {
                ch.isLetter() || ch == '.' -> output.append(ch.lowercaseChar())
                SUPERSCRIPTS.containsKey(ch) -> output.append('^').append(SUPERSCRIPTS.getValue(ch))
                ch.isDigit() || ch == '^' -> output.append(ch)
            }
        }
        return output.toString().replace("^^", "^")
    }

    private fun normalizeChemicalForDetection(value: String): String = buildString {
        value.trim().forEach { ch ->
            when {
                ch.isLetterOrDigit() || ch in "()" -> append(ch)
                SUBSCRIPTS.containsKey(ch) -> append(SUBSCRIPTS.getValue(ch))
            }
        }
    }

    private fun isScriptCharacter(ch: Char): Boolean = ch in SUPERSCRIPTS || ch in SUBSCRIPTS

    private fun isMathSymbol(ch: Char): Boolean = ch in setOf(
        '√', '∑', '∫', '∞', 'π', 'θ', 'φ', 'λ', 'δ', 'Δ', 'ω', '°', '½'
    ) || isScriptCharacter(ch)

    private fun isSimpleAtomCharacter(ch: Char): Boolean =
        ch.isLetterOrDigit() || isScriptCharacter(ch) || ch in ".,'′πθφλδΔω∞"

    private fun isAtomStartingCharacter(ch: Char): Boolean =
        isSimpleAtomCharacter(ch) || ch == '\\' || ch == '√' || ch in "([{⟨"

    private fun isAtomEndingCharacter(ch: Char): Boolean =
        isSimpleAtomCharacter(ch) || ch in ")]}⟩"
}
