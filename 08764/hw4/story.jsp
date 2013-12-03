<jsp:include page="template-top.jsp" />
<link rel="stylesheet" href="css/signup.css" type="text/css"/>
<!-- head -->
<div class="body clearfix">
	<div class="signup">
		<form action="story.do" method="post" class="grey p20" name="story">
			<input type="hidden" name="form" value="story" />
			<fieldset>
				<legend>Post Weird </legend>
				<label for="story">weird: </label>
				<textarea name="story" rows="8" cols="70" id="story"></textarea>
				<div class="description"></div>
			</fieldset>
			<div class="item1 clearfix">
				<input value="Weird!" type="submit" class="g-box" /> <input
					value="Clear" type="reset" class="reset" />
			</div>
		</form>
	</div>
	<div class="signin">
		No Comment, Pity! <input type="button" value="Go Back Home!"
			class="g-box2 ghome" />
	</div>
</div>
<!-- body -->
<jsp:include page="template-bottom.jsp" />