<jsp:include page="template-top.jsp" />
<link rel="stylesheet" href="css/signup.css" type="text/css"/>
<!-- head -->
<div class="body clearfix">
	<div class="signup">
		<form action="comment.do" method="post" class="grey p20" name="comment">
		<input type="hidden" name="form" value = "story"/>
		<input type="hidden" name="sid" value = "${ sid }"/>
			<fieldset>
				<legend>Post Comment </legend>
				<label for="comment">comment: </label>
				<textarea name="comment" rows="8" cols="70" id="comment"></textarea>
				<div class="description"></div>
			</fieldset>
			<div class="item1 clearfix">
				<input value="Comment!" type="submit" class="g-box" /> <input
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