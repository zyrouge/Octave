package xyz.gnarbot.gnar.utils

import net.dv8tion.jda.core.entities.MessageEmbed
import net.dv8tion.jda.core.entities.MessageEmbed.*
import net.dv8tion.jda.core.entities.User
import net.dv8tion.jda.core.entities.impl.MessageEmbedImpl
import java.awt.Color
import java.time.*
import java.time.temporal.TemporalAccessor
import java.util.*

/**
 * Builder system used to build [MessageEmbeds][net.dv8tion.jda.core.entities.MessageEmbed].
 * <br>A visual breakdown of an Embed and how it relates to this class is available at
 * [http://imgur.com/a/yOb5n](http://imgur.com/a/yOb5n).
 */
@Suppress("UNCHECKED_CAST", "NOTHING_TO_INLINE")
abstract class AbstractEmbedBuilder<T : AbstractEmbedBuilder<T>> {
    /**
     * Creates an EmbedBuilder to be used to creates an embed to send.
     * <br>Every part of an embed can be removed or cleared by providing {@code null} to the setter method.
     */
    constructor()

    /**
     * Creates an EmbedBuilder using fields in an existing embed.
     *
     * @param  embed
     *         the existing embed
     */
    constructor(embed: MessageEmbed) {
        url = embed.url
        title = embed.title
        descriptionBuilder.append(embed.description)
        timestamp = embed.timestamp
        color = embed.color
        thumbnail = embed.thumbnail
        author = embed.author
        footer = embed.footer
        image = embed.image
        if (embed.fields.isNotEmpty()) fields.addAll(embed.fields)
    }

    private var url: String? = null

    private var title: String? = null

    var descriptionBuilder: StringBuilder = StringBuilder()
        private set

    private var timestamp: OffsetDateTime? = null

    private var color: Color? = null

    private var thumbnail: Thumbnail? = null

    private var author: AuthorInfo? = null

    private var footer: Footer? = null

    private var image: ImageInfo? = null

    val fields: MutableList<Field> = LinkedList()

    /**
     * Returns a [MessageEmbed][net.dv8tion.jda.core.entities.MessageEmbed]
     * that has been checked as being valid for sending.
     *
     * @throws java.lang.IllegalStateException
     *         If the embed is empty. Can be checked with [.isEmpty].
     *
     *
     * @return the built, sendable [net.dv8tion.jda.core.entities.MessageEmbed]
     */
    fun build(): MessageEmbed {
        check(!isEmpty) { "Cannot build an empty embed!" }
        check(descriptionBuilder.length <= TEXT_MAX_LENGTH) { "Description is longer than $TEXT_MAX_LENGTH! Please limit your input!" }
        val description = if (this.descriptionBuilder.isEmpty()) null else this.descriptionBuilder.toString()

        return MessageEmbedImpl()
                .setTitle(title)
                .setUrl(url)
                .setDescription(description)
                .setTimestamp(timestamp)
                .setColor(color)
                .setThumbnail(thumbnail)
                .setAuthor(author)
                .setFooter(footer)
                .setImage(image)
                .setFields(fields)
    }

    /**
     * Checks if the given embed is empty. Empty embeds will throw an exception if built
     * 
     * @return true if the embed is empty and cannot be built
     */
    val isEmpty: Boolean
        get() = title == null
                && descriptionBuilder.isEmpty()
                && timestamp == null
                && color == null
                && thumbnail == null
                && author == null
                && footer == null
                && image == null
                && fields.isEmpty()

    /**
     * Sets the Title of the embed.
     *
     * [Example](http://i.imgur.com/JgZtxIM.png)
     * @param  title
     *         the title of the embed
     *
     * @param  url
     *         Makes the title into a hyperlink pointed at this url.
     *
     *
     * @throws java.lang.IllegalArgumentException
     *
     *              * If the provided `title` is an empty String.
     *              * If the length of `title` is greater than [net.dv8tion.jda.core.EmbedBuilder.TITLE_MAX_LENGTH].
     *              * If the length of `url` is longer than [net.dv8tion.jda.core.EmbedBuilder.URL_MAX_LENGTH].
     *              * If the provided `url` is not a properly formatted http or https url.
     *
     *
     *
     * @return the builder after the title has been set
     */
    @JvmOverloads
    fun setTitle(title: String?, url: String? = null): T {
        if (title == null) {
            this.title = null
            this.url = null
        } else {
            if (title.isEmpty())
                throw IllegalArgumentException("Title cannot be empty!")
            if (title.length > TITLE_MAX_LENGTH)
                throw IllegalArgumentException("Title cannot be longer than $TITLE_MAX_LENGTH characters.")
            urlCheck(url)

            this.title = title
            this.url = url
        }
        return this as T
    }

    fun getTitle() = title
    fun getUrl() = url

    fun setUrl(url: String?): T {
        urlCheck(url)
        setTitle(getTitle(), url)
        return this as T
    }

    /**
     * Sets the Description of the embed. This is where the main chunk of text for an embed is typically placed.
     *
     *
     * * [Example](http://i.imgur.com/lbchtwk.png)
     *
     * @param  description
     *         the description of the embed, `null` to reset
     *
     *
     * @throws java.lang.IllegalArgumentException
     *         If the length of `description` is greater than [net.dv8tion.jda.core.EmbedBuilder.TEXT_MAX_LENGTH]
     *
     *
     * @return the builder after the description has been set
     */
    fun setDescription(description: CharSequence?): T {
        if (description == null || description.isEmpty()) {
            this.descriptionBuilder = StringBuilder()
        } else {
            require(description.length <= TEXT_MAX_LENGTH) { "Description cannot be longer than $TEXT_MAX_LENGTH characters." }
            this.descriptionBuilder = StringBuilder(description)
        }
        return this as T
    }

    fun getDescription() = descriptionBuilder.toString()

    fun setDescription(any: Any?): T {
        return setDescription(any.toString())
    }

    inline fun description(block: () -> String?): T {
        descriptionBuilder.append(block())
        return this as T
    }

    /**
     * Appends to the description of the embed. This is where the main chunk of text for an embed is typically placed.
     * 
     *
     * [Example](http://i.imgur.com/lbchtwk.png)
     *
     * @param  description
     *         the string to append to the description of the embed
     *
     *
     * @throws java.lang.IllegalArgumentException
     *
     *              * If the provided `description` String is null
     *              * If the length of `description` is greater than [net.dv8tion.jda.core.EmbedBuilder.TEXT_MAX_LENGTH].
     *
     *
     *
     * @return the builder after the description has been set
     */
    fun appendDescription(description: CharSequence?): T {
        requireNotNull(description)
        require(this.descriptionBuilder.length + description!!.length <= TEXT_MAX_LENGTH) {
            "Description cannot be longer than $TEXT_MAX_LENGTH characters."
        }
        this.descriptionBuilder.append(description)
        return this as T
    }

    /**
     * Sets the Timestamp of the embed.
     *
     * [Example](http://i.imgur.com/YP4NiER.png)
     *
     * **Hint:* ** You can get the current time using [Instant.now()][java.time.Instant.now] or convert time from a
     * millisecond representation by using [Instant.ofEpochMilli(long)][java.time.Instant.ofEpochMilli];
     * @param  temporal
     *         the temporal accessor of the timestamp
     *
     *
     * @return the builder after the timestamp has been set
     */
    fun setTimestamp(temporal: TemporalAccessor?): T {
        if (temporal == null) {
            this.timestamp = null
        } else if (temporal is OffsetDateTime) {
            this.timestamp = temporal
        } else {
            var offset: ZoneOffset
            try {
                offset = ZoneOffset.from(temporal)
            } catch (ignore: DateTimeException) {
                offset = ZoneOffset.UTC
            }

            try {
                val ldt = LocalDateTime.from(temporal)
                this.timestamp = OffsetDateTime.of(ldt, offset)
            } catch (ignore: DateTimeException) {
                try {
                    val instant = Instant.from(temporal)
                    this.timestamp = OffsetDateTime.ofInstant(instant, offset)
                } catch (ex: DateTimeException) {
                    throw DateTimeException("Unable to obtain OffsetDateTime from TemporalAccessor: " +
                            temporal + " of type " + temporal.javaClass.name, ex)
                }

            }

        }
        return this as T
    }

    fun getTimestamp() = timestamp

    /**
     * Sets the Color of the embed.
     *
     * [Example](http://i.imgur.com/2YnxnRM.png)
     *
     * **Hint:* ** You can use a predefined color like [java.awt.Color.BLUE] or you can define
     * your own color using one of Color's constructors.
     * <br></br>Example: [new Color(0, 0, 255)][java.awt.Color.Color]. This is the same as [java.awt.Color.BLUE]
     * @param  color
     *         the color of the embed
     *
     *
     * @return the builder after the color has been set
     */
    fun setColor(color: Color?): T {
        this.color = color
        return this as T
    }

    fun getColor() = color

    /**
     * Sets the Thumbnail of the embed.
     *
     * [Example](http://i.imgur.com/Zc3qwqB.png)
     * @param  url
     *         the url of the thumbnail of the embed
     *
     *
     * @throws java.lang.IllegalArgumentException
     *
     *              * If the length of `url` is longer than [net.dv8tion.jda.core.EmbedBuilder.URL_MAX_LENGTH].
     *              * If the provided `url` is not a properly formatted http or https url.
     *
     *
     *
     * @return the builder after the thumbnail has been set
     */
    fun setThumbnail(url: String?): T {
        if (url == null) {
            this.thumbnail = null
        } else {
            urlCheck(url)
            this.thumbnail = Thumbnail(url, null, 0, 0)
        }
        return this as T
    }

    fun getThumbnail() = thumbnail?.url


    /**
     * Sets the Image of the embed.
     *
     * [Example](http://i.imgur.com/2hzuHFJ.png)
     * @param  url
     *         the url of the image of the embed
     *
     *
     * @throws java.lang.IllegalArgumentException
     *
     *              * If the length of `url` is longer than [net.dv8tion.jda.core.EmbedBuilder.URL_MAX_LENGTH].
     *              * If the provided `url` is not a properly formatted http or https url.
     *
     *
     *
     * @return the builder after the image has been set
     */
    fun setImage(url: String?): T {
        if (url == null) {
            this.image = null
        } else {
            urlCheck(url)
            this.image = ImageInfo(url, null, 0, 0)
        }
        return this as T
    }

    fun getImage() = image?.url

    /**
     * Sets the Author of the embed. The author appears in the top left of the embed and can have a small
     * image beside it along with the author's name being made clickable by way of providing a url.
     *
     * [Example](http://i.imgur.com/JgZtxIM.png)
     * @param  name
     *         the name of the author of the embed. If this is not set, the author will not appear in the embed
     *
     * @param  url
     *         the url of the author of the embed
     *
     * @param  iconUrl
     *         the url of the icon for the author
     *
     *
     * @throws java.lang.IllegalArgumentException
     *
     *              * If the length of `url` is longer than [net.dv8tion.jda.core.EmbedBuilder.URL_MAX_LENGTH].
     *              * If the provided `url` is not a properly formatted http or https url.
     *              * If the length of `iconUrl` is longer than [net.dv8tion.jda.core.EmbedBuilder.URL_MAX_LENGTH].
     *              * If the provided `iconUrl` is not a properly formatted http or https url.
     *
     *
     *
     * @return the builder after the author has been set
     */
    fun setAuthor(name: String?, url: String?, iconUrl: String?): T {
        //We only check if the name is null because its presence is what determines if the
        // the author will appear in the embed.
        if (name == null) {
            this.author = null
        } else {
            urlCheck(url)
            urlCheck(iconUrl)
            this.author = AuthorInfo(name, url, iconUrl, null)
        }
        return this as T
    }

    fun setAuthor(user: User): T {
        return setAuthor(user.name, null, user.avatarUrl)
    }

    fun getAuthor() = author

    /**
     * Sets the Footer of the embed.
     *
     * [Example](http://i.imgur.com/jdf4sbi.png)
     * @param  text
     *         the text of the footer of the embed. If this is not set, the footer will not appear in the embed.
     *
     * @param  iconUrl
     *         the url of the icon for the footer
     *
     *
     * @throws java.lang.IllegalArgumentException
     *
     *              * If the length of `text` is longer than [net.dv8tion.jda.core.EmbedBuilder.TEXT_MAX_LENGTH].
     *              * If the length of `iconUrl` is longer than [net.dv8tion.jda.core.EmbedBuilder.URL_MAX_LENGTH].
     *              * If the provided `iconUrl` is not a properly formatted http or https url.
     *
     *
     *
     * @return the builder after the footer has been set
     */
    @JvmOverloads
    fun setFooter(text: String?, iconUrl: String? = null): T {
        //We only check if the text is null because its presence is what determines if the
        // footer will appear in the embed.
        if (text == null) {
            this.footer = null
        } else {
            require(text.length <= TEXT_MAX_LENGTH) {
                "Text cannot be longer than $TEXT_MAX_LENGTH characters."
            }
            urlCheck(iconUrl)
            this.footer = Footer(text, iconUrl, null)
        }
        return this as T
    }

    fun getFooter() = footer

    /**
     * Copies the provided Field into a new Field for this builder.
     * <br></br>For additional documentation, see [.field]
     * @param  field
     *         the field object to add
     *
     *
     * @return the builder after the field has been added
     */
    fun field(field: Field?): T = if (field == null) this as T else field(field.name, field.isInline, field.value)

    /**
     * Adds a Field to the embed.
     *
     * Note: If a blank string is provided to either `name` or `value`, the blank string is replaced
     * with [net.dv8tion.jda.core.AbstractEmbedBuilder.ZERO_WIDTH_SPACE].
     *
     * [Example of Inline](http://i.imgur.com/gnjzCoo.png)
     *
     * [Example if Non-inline](http://i.imgur.com/Ky0KlsT.png)
     * @param  name
     *         the name of the Field, displayed in bold above the `value`.
     *
     * @param  value
     *         the contents of the field.
     *
     * @param  inline
     *         whether or not this field should display inline.
     *
     * @throws java.lang.IllegalArgumentException
     *              * If only `name` or `value` is set. Both must be set.
     *              * If the length of `name` is greater than [net.dv8tion.jda.core.EmbedBuilder.TITLE_MAX_LENGTH].
     *              * If the length of `value` is greater than [net.dv8tion.jda.core.EmbedBuilder.TEXT_MAX_LENGTH].
     *
     * @return the builder after the field has been added
     */
    @JvmOverloads
    fun field(name: String = ZERO_WIDTH_SPACE, inline: Boolean = false, value: String = ZERO_WIDTH_SPACE): T {
        var n = name
        var v = value

        require(n.length <= TITLE_MAX_LENGTH) { "Name cannot be longer than $TITLE_MAX_LENGTH characters." }
        require(v.length <= VALUE_MAX_LENGTH) { "Value cannot be longer than $VALUE_MAX_LENGTH characters." }

        if (n.isEmpty()) n = ZERO_WIDTH_SPACE
        if (v.isEmpty()) v = ZERO_WIDTH_SPACE

        this.fields += Field(n, v, inline)

        return this as T
    }

    inline fun field(inline: Boolean): T = field(ZERO_WIDTH_SPACE, inline, ZERO_WIDTH_SPACE)

    inline fun field(name: String, inline: Boolean = false, value: Any?): T
            = field(name, inline, value.toString())

    inline fun field(name: String, inline: Boolean = false, block: () -> String?): T
            = field(name, inline, block())

    /**
     * Adds a blank (empty) Field to the embed.
     *
     * [Example of Inline](http://i.imgur.com/tB6tYWy.png)
     *
     * [Example of Non-inline](http://i.imgur.com/lQqgH3H.png)
     * @param  inline
     *         whether or not this field should display inline
     *
     * @return the builder after the field has been added
     */
    @Deprecated("Old method", ReplaceWith("field(inline)"))
    fun blankField(inline: Boolean = false): T = field(inline = inline)

    /**
     * Clears all fields from the embed, such as those created with the
     * [EmbedBuilder(MessageEmbed)][net.dv8tion.jda.core.EmbedBuilder.EmbedBuilder]
     * constructor or via the
     * [addField][net.dv8tion.jda.core.EmbedBuilder.field] methods.
     *
     * @return the builder after the field has been added
     */
    fun clearFields(): T {
        this.fields.clear()

        return this as T
    }

    inline fun inline(block: InlineHandler<T>.() -> Unit) {
        InlineHandler(this).block()
    }

    class InlineHandler<T : AbstractEmbedBuilder<T>>(val embedBuilder: AbstractEmbedBuilder<T>) {
        inline fun field(name: String, value: String?): T
                = embedBuilder.field(name, true, value)

        inline fun field(name: String, value: Any?): T
                = embedBuilder.field(name, true, value.toString())

        inline fun field(name: String = ZERO_WIDTH_SPACE, block: () -> String?): T
                = embedBuilder.field(name, true, block())

        inline fun field() = embedBuilder.field(true)
    }

    companion object {
        @JvmStatic fun urlCheck(url: String?) {
            if (url == null) return
            require(url.length <= URL_MAX_LENGTH) { "URL cannot be longer than $URL_MAX_LENGTH characters." }
            require(URL_PATTERN matches url) { "URL must be a valid http or https url." }
        }

        @JvmField val ZERO_WIDTH_SPACE = "\u200E"
        @JvmField val URL_PATTERN = Regex("""\s*(https?|attachment)://.+\..{2,}\s*""", RegexOption.IGNORE_CASE)
    }
    
    inline var AbstractEmbedBuilder<*>.title : String?
        get() = this.getTitle()
        set(value) {
            this.setTitle(value)
        }

    inline var AbstractEmbedBuilder<*>.url : String?
        get() = this.getUrl()
        set(value) {
            this.setTitle(getTitle(), value)
        }

    inline var AbstractEmbedBuilder<*>.color : Color?
        get() = this.getColor()
        set(value) {
            this.setColor(value)
        }

    inline var AbstractEmbedBuilder<*>.description : String?
        get() = this.getDescription()
        set(value) {
            this.setDescription(value)
        }

    inline var AbstractEmbedBuilder<*>.footer : String?
        get() = this.getFooter()?.text
        set(value) {
            this.setFooter(value)
        }

    inline var AbstractEmbedBuilder<*>.image : String?
        get() = this.getImage()
        set(value) {
            this.setImage(value)
        }

    inline var AbstractEmbedBuilder<*>.thumbnail : String?
        get() = this.getThumbnail()
        set(value) {
            this.setThumbnail(value)
        }

    inline var AbstractEmbedBuilder<*>.timestamp : OffsetDateTime?
        get() = this.getTimestamp()
        set(value) {
            this.setTimestamp(value)
        }
}